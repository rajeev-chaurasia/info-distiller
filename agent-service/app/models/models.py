from pydantic import BaseModel, HttpUrl

class URLRequest(BaseModel):
    url: HttpUrl # This ensures the 'url' field is a valid URL