import ffmpeg
import librosa
from scipy.io import wavfile
import noisereduce as nr
from django.core.exceptions import ValidationError
from django.shortcuts import render, redirect
from django.http import HttpResponse
from django.conf import settings
from django.http import JsonResponse
import speech_recognition as sr
from .forms import AudioFileForm
from rest_framework import status
from django.views.decorators.csrf import csrf_exempt
from rest_framework.decorators import api_view
from rest_framework.response import Response
from pydub import AudioSegment
from subprocess import run, PIPE
import os


@csrf_exempt
def upload_audio(request):
    if request.method == 'POST':
        form = AudioFileForm(request.POST, request.FILES)
        if form.is_valid():
            audio_file = form.save()
            request.session['output_format'] = form.cleaned_data['output_format']
            return redirect('convert_audio', pk=audio_file.pk)
    else:
        form = AudioFileForm()
    return render(request, 'upload_audio.html', {'form': form})

# def convert_audio(request, pk):
#     audio_file = AudioFile.objects.get(pk=pk)
#     output_format = request.session.get('output_format', 'ogg')
    
#     input_path = audio_file.audio.path
#     output_path = f"{input_path}.{output_format}"
    
#     ffmpeg.input(input_path).output(output_path).run()

#     return FileResponse(open(output_path, 'rb'), as_attachment=True, filename=f'converted_audio.{output_format}')
@csrf_exempt
@api_view(['POST'])
def convert_audio(request):
    if request.method == 'POST':
        audio_file = request.FILES.get('audio')
        output_format = request.data.get('format', 'mp3').lower()
        file_name = audio_file.name.split('.')[0]
        print("outputformat:", output_format)

        if not audio_file:
            return Response({'error': 'No audio file provided'}, status=status.HTTP_400_BAD_REQUEST)
    
        # Validate the desired output format
        if output_format not in ['mp3', 'wav', 'ogg']:
            return Response({'error': 'Invalid output format'}, status=status.HTTP_400_BAD_REQUEST)

        input_path = os.path.join(settings.MEDIA_ROOT, 'converted', audio_file.name)
        output_path = os.path.join(settings.MEDIA_ROOT, 'converted',  file_name + '.' + output_format)
        if os.path.exists(output_path):
            os.remove(output_path)
        with open(input_path, 'wb+') as f:
            for chunk in audio_file.chunks():
                f.write(chunk)
        # Convert audio
        try:
            ffmpeg.input(input_path).output(output_path).run()
        except ffmpeg.Error as e:
            raise ValidationError({'error': f'FFmpeg conversion failed: {e}'})
        os.remove(input_path)
        download_url = f"/converted/{file_name + '.' + output_format}"
        return Response({
            'message': 'Audio converted successfully',
            'download_url': download_url
        })

    return Response({'error': 'Invalid request'})

def download_file(request, filepath):
    # Construct the full absolute path
    file_path = os.path.join(settings.MEDIA_ROOT, filepath)
    
    # Normalize the file path to prevent directory traversal
    file_path = os.path.normpath(file_path)
    response = None 
    # Check if file exists
    if os.path.exists(file_path):
        with open(file_path, 'rb') as file:
            response = HttpResponse(file.read(), content_type="application/octet-stream")
            response['Content-Disposition'] = f'attachment; filename="{os.path.basename(file_path)}"'
        os.remove(file_path)
        if response:
            return response
        else:
            return HttpResponse("File not found.", status=404)
    else:
        return HttpResponse("File not found.", status=404)

@csrf_exempt
def speech_to_text(request):
    if request.method == 'POST' and 'audio' in request.FILES:
        uploaded_file = request.FILES['audio']
        file_extension = os.path.splitext(uploaded_file.name)[1].lower()
        tmp_filepath = os.path.join(settings.MEDIA_ROOT, 'audios', uploaded_file.name)
        with open(tmp_filepath, 'wb') as f:
                for chunk in uploaded_file.chunks():
                    f.write(chunk)

        if file_extension not in [".flac", ".wav"]:            
            # converted to .flac
            converted_filepath = os.path.splitext(tmp_filepath)[0] + '.flac'
            command = ['ffmpeg', '-i', tmp_filepath, '-y', '-vn', '-ar', '16000', '-ac', '1', converted_filepath]
            result = run(command, stdout=PIPE, stderr=PIPE, text=True)
         
            os.remove(tmp_filepath)
            if result.returncode != 0:
                return JsonResponse({'status': 'error', 'message': 'Failed to convert audio to FLAC: ' + result.stderr})
            tmp_filepath = converted_filepath
        recognizer = sr.Recognizer()
        try:
            with sr.AudioFile(tmp_filepath) as source:
                audio_data = recognizer.record(source)
                text = recognizer.recognize_google(audio_data, language='vi-VN')
            os.remove(tmp_filepath)  # Clean up temporary file
            print(text)
            return JsonResponse({'status': 'success', 'text': text})
        except sr.UnknownValueError:
            os.remove(tmp_filepath)
            return JsonResponse({'status': 'error', 'message': 'Speech recognition could not understand audio'})
        except sr.RequestError as e:
            os.remove(tmp_filepath)
            return JsonResponse({'status': 'error', 'message': 'Could not request results; {0}'.format(e)})
        except Exception as e:  # Catch generic exceptions for unexpected errors
            os.remove(tmp_filepath)
            return JsonResponse({'status': 'error', 'message': 'An unexpected error occurred: {0}'.format(e)})
    return JsonResponse({'status': 'error', 'message': 'Invalid request or no files uploaded'})

@csrf_exempt
@api_view(['POST'])
def remove_silence(request):
    if request.method == 'POST':
        audio_file = request.FILES.get('audio')

        if not audio_file:
            return Response({'error': 'No audio file provided'}, status=status.HTTP_400_BAD_REQUEST)

        input_path = os.path.join(settings.MEDIA_ROOT, 'audios', audio_file.name)
        output_path = os.path.join(settings.MEDIA_ROOT, 'processed', audio_file.name)
        
        # Write the uploaded file to the input path
        with open(input_path, 'wb+') as f:
            for chunk in audio_file.chunks():
                f.write(chunk)

        # Construct the ffmpeg command to remove silence
        try:
            (
                ffmpeg
                .input(input_path)
                .output(output_path, **{
                            'af': 'silenceremove=start_periods=1:stop_periods=-1:start_threshold=-30dB'
                    })
                .run(capture_stdout=True, capture_stderr=True)
            )
        except ffmpeg.Error as e:
            os.remove(input_path)  # Clean up input file
            return Response({'error': 'FFmpeg error: ' + e.stderr.decode('utf8')}, status=status.HTTP_400_BAD_REQUEST)

        os.remove(input_path)  # Clean up input file

        # Respond with a success message and a URL to access the processed file
        download_url = f"/processed/{audio_file.name}"
        return Response({
            'message': 'Silence removed successfully',
            'download_url': download_url
        })

    return Response({'error': 'Invalid request method'}, status=status.HTTP_400_BAD_REQUEST)

@csrf_exempt
@api_view(['POST'])
def upload_file(request):
    if request.method == 'POST':
        audio_file = request.FILES.get('audio')

        if not audio_file:
            return Response({'error': 'No audio file provided'}, status=status.HTTP_400_BAD_REQUEST)

        file_name = audio_file.name.split('.')[0]
        input_path = os.path.join(settings.MEDIA_ROOT, 'audios', audio_file.name)
        
        # Write the uploaded file to the input path
        with open(input_path, 'wb+') as f:
            for chunk in audio_file.chunks():
                f.write(chunk)

        # Respond with a success message and a URL to access the processed file
        download_url = f"/audios/{file_name}"
        return Response({
            'message': 'Silence removed successfully',
            'download_url': download_url
        })

    return Response({'error': 'Invalid request method'}, status=status.HTTP_400_BAD_REQUEST)

@api_view(['POST'])
def reduce_noise(request):
    if request.method == 'POST':
        audio_file = request.FILES.get('audio')
        if not audio_file:
            return Response({'error': 'No audio file provided'}, status=status.HTTP_400_BAD_REQUEST)

        # Save the original audio to disk temporarily
        file_name = audio_file.name
        input_path = os.path.join(settings.MEDIA_ROOT, 'audios', file_name)
        output_path = os.path.join(settings.MEDIA_ROOT, 'processed', file_name)

        with open(input_path, 'wb+') as f:
            for chunk in audio_file.chunks():
                f.write(chunk)

        # Load audio file
        data, rate = librosa.load(input_path, sr=None)

        # Perform noise reduction
        reduced_noise_audio = nr.reduce_noise(y=data, sr=rate)
        duration_ms = (len(reduced_noise_audio) / rate) * 1000


        # Write the processed data back to a file
        wavfile.write(output_path, rate, reduced_noise_audio)

        # Clean up: remove the original file after processing
        os.remove(input_path)

        # Create a response
        download_url = f"/processed/{file_name}"
        return Response({
            'message': 'Noise reduced successfully',
            'download_url': download_url,
            'trimmed_duration_ms': int(duration_ms)
        })

    return Response({'error': 'Invalid request method'}, status=status.HTTP_400_BAD_REQUEST)

@api_view(['POST'])
def trim_audio(request):
    if request.method == 'POST':
        audio_file = request.FILES.get('audio')
        start_time = request.data.get('startTime')  # Start time in milliseconds
        end_time = request.data.get('endTime')  # End time in milliseconds
        print(start_time, end_time)
        print(audio_file)

        if not audio_file:
            return JsonResponse({'error': 'No audio file provided'}, status=status.HTTP_400_BAD_REQUEST)
    
        if not start_time or not end_time:
            return JsonResponse({'error': 'Start and end times are required'}, status=status.HTTP_400_BAD_REQUEST)
    
        # Convert start and end times to integers
        try:
            start_time = int(start_time)
            end_time = int(end_time)
        except ValueError:
            return JsonResponse({'error': 'Invalid start or end time provided'}, status=status.HTTP_400_BAD_REQUEST)
    
        original_extension = os.path.splitext(audio_file.name)[1].lower()
        file_name = audio_file.name.split('.')[0]
        input_path = os.path.join(settings.MEDIA_ROOT, 'audios', audio_file.name)
        wav_path = os.path.join(settings.MEDIA_ROOT, 'audios', f'{file_name}.wav')
        trimmed_path = os.path.join(settings.MEDIA_ROOT, 'processed', f'{file_name}.wav')
        final_output_path = os.path.join(settings.MEDIA_ROOT, 'processed', f'{file_name}{original_extension}')

        # Save the original audio to disk temporarily
        with open(input_path, 'wb+') as f:
            for chunk in audio_file.chunks():
                f.write(chunk)

        # Convert to WAV if necessary
        if original_extension != '.wav':
            command = ['ffmpeg', '-i', input_path, '-acodec', 'pcm_s16le', '-ac', '1', '-ar', '44100', wav_path]
            conversion_result = run(command, stdout=PIPE, stderr=PIPE, text=True)
            if conversion_result.returncode != 0:
                return JsonResponse({'error': 'Failed to convert to WAV: ' + conversion_result.stderr}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
            os.remove(input_path)  
        else:
            wav_path = input_path  # If already WAV, use original

        try:
            # Load WAV audio file with Pydub
            audio = AudioSegment.from_file(wav_path)
            # Trim audio
            trimmed_audio = audio[start_time:end_time]
            # Export trimmed audio
            trimmed_audio.export(trimmed_path, format='wav')

            trimmed_duration = len(trimmed_audio)

            # Convert back to original format
            if original_extension != '.wav':
                convert_command = ['ffmpeg', '-i', trimmed_path, final_output_path]
                convert_result = run(convert_command, stdout=PIPE, stderr=PIPE, text=True)
                if convert_result.returncode != 0:
                    raise Exception(f'Failed to convert back to original format: {convert_result.stderr}')
        except Exception as e:
            return JsonResponse({'error': 'Error processing audio file', 'details': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        finally:
            os.remove(wav_path)  # Cleanup on failure
            if os.path.exists(trimmed_path):
                os.remove(trimmed_path)  # Cleanup trimmed WAV file

        # Create a response
        download_url = f"/processed/{file_name}{original_extension}"
        return JsonResponse({
            'message': 'Audio trimmed and converted successfully',
            'download_url': download_url,
            'trimmed_duration_ms': trimmed_duration
        })

    return JsonResponse({'error': 'Invalid request method'}, status=status.HTTP_400_BAD_REQUEST)