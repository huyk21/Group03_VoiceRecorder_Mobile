# Generated by Django 5.0.4 on 2024-04-29 16:40

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('process_file', '0002_audiofile_converted_audio'),
    ]

    operations = [
        migrations.AlterField(
            model_name='audiofile',
            name='converted_audio',
            field=models.FileField(blank=True, max_length=500, null=True, upload_to='converted/'),
        ),
    ]
