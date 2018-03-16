План:
1. консольное приложение
2. подключение к гугл диску
3. Добавить команды
  - получить список файлов
  - добавить файл
  
OAuth 2.0

1. project id - public, client secret - private
2. bearer token

https://accounts.google.com/o/oauth2/v2/auth?
 scope=https://www.googleapis.com/auth/drive&
 response_type=code&
 state=security_token%3D138r5719ru3e1%26url%3Dhttps://oauth2.example.com/token&
 redirect_uri=urn:ietf:wg:oauth:2.0:oob&
 client_id=359097715554-9l17d9b86g2h84cvtk23aarjtae690ud.apps.googleusercontent.com
 