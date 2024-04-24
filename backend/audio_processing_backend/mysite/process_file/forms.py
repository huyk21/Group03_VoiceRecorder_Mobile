from django import forms
from .models import AudioFile

class AudioFileForm(forms.ModelForm):
    OUTPUT_CHOICES = [
        ('mp3', 'MP3'),
        ('wav', 'WAV'),
        ('ogg', 'OGG'),
    ]
    
    output_format = forms.ChoiceField(choices=OUTPUT_CHOICES, required=True)

    class Meta:
        model = AudioFile
        fields = ['audio']
