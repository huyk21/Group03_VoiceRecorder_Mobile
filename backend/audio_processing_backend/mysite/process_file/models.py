from django.db import models

# Create your models here.
class AudioFile(models.Model):
    audio = models.FileField(upload_to='audios/')
    converted_audio = models.FileField(upload_to='converted/', blank=True, null=True, max_length=500)
