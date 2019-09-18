curl -X POST \
  http://localhost:18989/oauth/token \
  -H 'Authorization: Basic YXBwbGljYXRpb246cGFzcw==' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'username=user@gmail.com&password=user&grant_type=password'
