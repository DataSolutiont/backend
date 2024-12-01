# Оглавление

- [Что это](#чтоЭто)
- [Как поднять](#какПоднять)
- [Как пользоваться](#какПользоваться)
- [Swagger](#сваггер)

# <a name="чтоЭто">Что это</a>

Это репозиторий со всеми на данный момент бэкенд-сервисами

# <a name="какПоднять">Как поднять</a>

Для начала нужно создать сеть docker:

```shell
docker network create backend-network
```

После этого нужно запустить все сервисы. В репозитории есть простейшие скрипты, чтобы не делать это вручную, но если у вам не Linux/MacOS, то вводим их сами:

> ![INFO]
> Если поднимаете в первый раз, то везде нужно добавить `--build`

1. Поднимаем сервис для резюме.

```shell
docker-compose -f ./cvservice/docker-compose.yaml up -d
```

2. Поднимаем сервис аутентификации:

```shell
docker-compose -f ./auth/docker-compose.yaml up -d
```

3. Поднимаем шлюз:

```shell
docker-compose -f ./gateway/docker-compose.yaml up -d
```

> ![WARNING]
> Перед использованием проверьте, что сервис для резюме поднялся.
> Он поднимается около 15 секунд из-за Elasticsearch, поэтому проверяйте логи контейнера!

# <a name="какПользоваться">Как пользоваться</a>

Все запросы должны идти через API-шлюз, который находится на порте `80`.

Ручки остаются те же, но работают через данный порт. Например:

Раньше для регистрации нужно было отправлять запрос на `localhost:8080/api/auth/signup`.
Сейчас для регистрации нужно отправить запрос на `localhost:80/api/auth/signup`.

# <a name="сваггер">Swagger</a>

Документация Swagger для каждого сервиса доступна пока только на них самих:

- Auth Service:
    `localhost:8080/api/auth/swagger-ui.html`
- CV Service:
    `localhost:8081/api/cvs/swagger-ui.html`

