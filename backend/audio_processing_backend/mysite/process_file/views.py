import ffmpeg
from django.core.exceptions import ValidationError
from django.shortcuts import render, redirect
from django.http import FileResponse, HttpResponse
from django.conf import settings
from django.core.files import File
from django.http import JsonResponse
import speech_recognition as sr
from .forms import AudioFileForm
from .models import AudioFile
from django.views.decorators.csrf import csrf_exempt
from rest_framework.decorators import api_view
from rest_framework.response import Response
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
        output_format = request.data.get('format', 'mp3')
        file_name = audio_file.name.split('.')[0]

        if not audio_file:
            return Response({'error': 'No audio file provided'}, status=status.HTTP_400_BAD_REQUEST)

        # Validate the desired output format
        if output_format not in ['mp3', 'wav', 'ogg']:
            return Response({'error': 'Invalid output format'}, status=status.HTTP_400_BAD_REQUEST)

        input_path = os.path.join(settings.MEDIA_ROOT, 'converted', audio_file.name)
        output_path = os.path.join(settings.MEDIA_ROOT, 'converted',  file_name + '.' + output_format)
        with open(input_path, 'wb+') as f:
                for chunk in audio_file.chunks():
                    f.write(chunk)
        print(input_path, output_path)
        # Convert audio
        try:
            ffmpeg.input(input_path).output(output_path).run()
        except ffmpeg.Error as e:
            raise ValidationError({'error': f'FFmpeg conversion failed: {e}'})
        ffmpeg.input(input_path).output(output_path).run()
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
    
    # Ensure the path stays within the media directory
    if not file_path.startswith(settings.MEDIA_ROOT):
        return HttpResponse("Unauthorized", status=401)

    # Check if file exists
    if os.path.exists(file_path):
        with open(file_path, 'rb') as file:
            response = HttpResponse(file.read(), content_type="application/octet-stream")
            response['Content-Disposition'] = f'attachment; filename="{os.path.basename(file_path)}"'
            return response
    else:
        return HttpResponse("File not found.", status=404)
    
@csrf_exempt
def speech_to_text(request):
    if request.method == 'POST' and 'audio' in request.FILES:
        uploaded_file = request.FILES['audio']
        file_extension = os.path.splitext(uploaded_file.name)[1].lower()
        tmp_filepath = os.path.join(settings.MEDIA_ROOT, 'audios', uploaded_file.name)
        with open(tmp_filepath, 'wb+') as f:
                for chunk in uploaded_file.chunks():
                    f.write(chunk)
        # Check file extension (simpler approach)
        if file_extension not in [".flac", ".wav"]:            
            # converted to .flac
            converted_filepath = os.path.splitext(tmp_filepath)[0] + '.flac'
            command = ['ffmpeg', '-i', tmp_filepath, '-y', '-vn', '-ar', '16000', '-ac', '1', converted_filepath]
            result = run(command, stdout=PIPE, stderr=PIPE, text=True)
         
            os.remove(tmp_filepath)
            if result.returncode != 0:
                return JsonResponse({'status': 'error', 'message': 'Failed to convert audio to FLAC: ' + result.stderr})
            tmp_filepath = converted_filepath
        # Speech recognition (using the temporary FLAC file)
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