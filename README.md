# Recipe API Spring Boot Application

This project is a Spring Boot RESTful API backend for managing a large collection of recipes. It supports importing recipes from a JSON file into a MySQL database and provides flexible endpoints for searching, filtering, sorting, and paginated retrieval of recipe data.

## Features

- Import and store recipe data with ingredients, instructions, nutrients, and more
- Retrieve recipes with pagination and sorting by rating
- Search recipes by multiple criteria: calories, title, cuisine, total cooking time, rating, etc.
- Combined search with pagination for scalable querying

## Installation

1. Clone the repository
2. Place `US_recipes_null.json` under `src/main/resources/`
3. Configure your MySQL database and update connection details in `application.properties`
4. Run the application with:

./mvnw spring-boot:run


## API Documentation

### Get All Recipes (Paginated)

**GET** `/api/recipes`

**Query Parameters:**

| Parameter | Type | Default | Description         |
|-----------|------|---------|---------------------|
| offSet    | int  | 1       | Page number (1-based) |
| pageSize  | int  | 10      | Number of recipes per page |

**Response:**

{
"offSet": 1,
"pageSize": 10,
"total": 8150,
"data": [
{
"id": 1,
"title": "Sweet Potato Pie",
"cuisine": "Southern Recipes",
"rating": 4.8,
"prepTime": 15,
"cookTime": 100,
"totalTime": 115,
"description": "Shared from a Southern recipe...",
"nutrients": "{"calories": "389 kcal", ...}",
"serves": "8 servings"
},
...
]
}


---

### Get All Recipes Sorted by Rating

**GET** `/api/recipes/rating`

Parameters and response same as above, but results sorted descending by rating.

---

### Search Recipes (Filter Only)

**GET** `/api/search`

**Query Parameters:** (all optional)

| Parameter       | Type   | Description                                                                 |
|-----------------|--------|-----------------------------------------------------------------------------|
| caloriesGreater | double | Filter calories greater than specified value                               |
| caloriesLess    | double | Filter calories less than specified value                                  |
| caloriesEqual   | double | Filter calories equal to specified value                                   |
| title           | string | Partial match on title                                                      |
| cuisine         | string | Exact match cuisine                                                         |
| totaltimeEqual  | int    | Filter total cooking time equal to value                                   |
| totaltimeGreater| int    | Filter total cooking time greater than value                               |
| totaltimeLess   | int    | Filter total cooking time less than value                                  |
| ratingGreater   | float  | Filter rating greater than value                                            |
| ratingLess      | float  | Filter rating less than value                                               |
| ratingEqual     | float  | Filter rating equal to value                                                |

**Response:**

List of recipes matching filters, no pagination.

---

### Search Recipes with Pagination

**GET** `/api/search/page`

Supports all filter parameters above plus:

| Parameter | Type | Default | Description           |
|-----------|------|---------|-----------------------|
| offSet    | int  | 1       | Page number (1-based)  |
| pageSize  | int  | 10      | Number of results/page |

**Response:**

Paginated and filtered recipes as in Get All Recipes.

---

## Example Requests

- Get second page of 5 recipes:

GET /api/recipes?offSet=2&pageSize=5


- Search for Southern recipes with rating >=4.5 and calories < 500:

GET /api/search?cuisine=Southern%20Recipes&ratingGreater=4.5&caloriesLess=500


- Paginated search for quick recipes with rating >= 4:

GET /api/search/page?totaltimeLess=20&ratingGreater=4&offSet=1&pageSize=10



