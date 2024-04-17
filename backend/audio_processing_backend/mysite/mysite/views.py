from django.http import JsonResponse

def get_names(request):
    # Logic to fetch or generate the list of names
    names = ["Alice", "Bob", "Charlie", "David"]  
    return JsonResponse({"names": names})
