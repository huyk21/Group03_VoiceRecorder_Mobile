import ffmpeg
import mimetypes
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
        output_format = request.data.get('format', 'mp3')  # Default to 'mp3' if no format is provided
        print(audio_file, output_format)
        if not audio_file:
            return Response({'error': 'No audio file provided'}, status=status.HTTP_400_BAD_REQUEST)

        # Validate the desired output format
        if output_format not in ['mp3', 'wav', 'ogg']:
            return Response({'error': 'Invalid output format'}, status=status.HTTP_400_BAD_REQUEST)
        
        audio_instance = AudioFile(audio=audio_file)
        audio_instance.save()

        input_path = audio_instance.audio.path
        output_path = os.path.join(settings.MEDIA_ROOT, 'audios', 'yourfile.' + output_format)

        # Convert audio
        ffmpeg.input(input_path).output(output_path).run()
        print(f'output_path {output_path}')
        # Save converted file
        with open(output_path, 'rb') as f:
            audio_instance.converted_audio.save(output_path.split('\\')[-1], File(f))

        return Response({
            'message': 'Audio converted successfully',
            'download_url': audio_instance.converted_audio.url
        })

    return Response({'error': 'Invalid request'}, status=status.HTTP_400_BAD_REQUEST)

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

        # Check file extension (simpler approach)
        if file_extension != ".flac":
            tmp_filepath = os.path.join(settings.MEDIA_ROOT, 'audios', uploaded_file.name)
            with open(tmp_filepath, 'wb+') as f:
                for chunk in uploaded_file.chunks():
                    f.write(chunk)

            converted_filepath = os.path.splitext(tmp_filepath)[0] + '.flac'
            command = ['ffmpeg', '-i', tmp_filepath, '-y', '-vn', '-ar', '16000', '-ac', '1', converted_filepath]
            result = run(command, stdout=PIPE, stderr=PIPE, text=True)
            if result.returncode != 0:
                os.remove(tmp_filepath)  # Clean up original file
                return JsonResponse({'status': 'error', 'message': 'Failed to convert audio to FLAC: ' + result.stderr})

            tmp_filepath = converted_filepath

        # Speech recognition (using the temporary FLAC file)
        recognizer = sr.Recognizer()
        try:
            with sr.AudioFile(tmp_filepath) as source:
                audio_data = recognizer.record(source)
                text = recognizer.recognize_google(audio_data)
            os.remove(tmp_filepath)  # Clean up temporary file
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