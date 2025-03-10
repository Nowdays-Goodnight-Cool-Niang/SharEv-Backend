# 도커 컴포즈를 이용하고 싶은 경우

> 로컬에 java가 설치되어 있지 않은 경우 유용합니다.\
> docker 설치 후 진행해 주세요.

디스코드에 올려둔 파일들을 `src/main/resources`에 첨부해주세요.

그 후

`docker compose --profile docker up --build`

해당 명령어 사용하시면 됩니다.

# 엔드포인트

## 카카오 로그인

http://localhost:8080/oauth2/authorization/kakao

## 로그아웃

http://localhost:8080/logout

## 그 외 엔드포인트

http://localhost:8080/swagger
