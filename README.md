# Kotlin JSON Server
Inspiration from JSON Server [https://github.com/typicode/json-server](https://github.com/typicode/json-server)

## Table of contents

<!-- toc -->

- [Getting started](#getting-started)
- [Routes](#routes)
    * [Plural routes](#plural-routes)
    * [Singular routes](#singular-routes)
    * [Filter](#filter)
    * [Paginate](#paginate)
    * [Sort](#sort)


- [Extras](#extras)
    * [Deployment](#deployment)

- [License](#license)

<!-- tocstop -->

## Getting started

Install Kotlin JSON Server

## Start JSON Server

gradlew run

## Routes

Based on the files in the models folder, here are all the default routes.

### Routes

```
GET    /posts
GET    /posts/1
POST   /posts
PUT    /posts/1
PATCH  /posts/1
DELETE /posts/1
```

### Filter

```
GET /posts?title=json-server
GET /posts?id=1
```

### Paginate

Use `_page` and optionally `_limit` to paginate returned data.

```
GET /posts?_page=7
GET /posts?_page=7&_limit=20
```

_10 items are returned by default_

### Sort

Add `_sort` and `_order` (ascending order by default)

```
GET /posts?_sort=views&_order=asc
GET /posts/1/comments?_sort=votes&_order=asc
```


## Extras

### Deployment

## License

MIT
