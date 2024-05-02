from django.urls import path, re_path
from . import views

urlpatterns = [
    path('upload/', views.upload_audio, name='upload_audio'),
    path('process_file/<int:pk>/', views.convert_audio, name='convert_audio'),
    path('convert/', views.convert_audio, name='convert_audio'),
    path('speech_to_text/', views.speech_to_text, name='speech_to_text'),
    path('remove_silence/', views.remove_silence, name='silence_detection'),
    path('reduce_noise/', views.reduce_noise, name='reduce_noise'),
    re_path(r'^download/(?P<filepath>.+)$', views.download_file, name='download_file')
]
