# CERN TODO Application
This is a skeleton of Spring Boot application which should be used as a start point to create a working one.
The goal of this task is to create simple web application which allows users to create TODOs via REST API.

Below you may find a proposition of the DB model:

![DB model](DBModel.png)

To complete the exercices please implement all missing classes and functonalites in order to be able to store and retrieve information about tasks and their categories.
Once you are ready, please send it to me (ie link to your git repository) before  our interview.

# Features
- Create, delete and update tasks
- Create, delete and update task categories
- Sort tasks by time or name
- Get tasks by category or name
- Get all (un)expired tasks

# Running
`./gradlew bootRun`

# End-points
An API manual describing the various end-points which are
available. All end-points are prepended with the current version of
the API. Currently the api is split into subgroups: `categories` and
`tasks`.

## Categories
TODOs can be grouped under categories which are a name and a
description of the category. A task can have only one category, but a
category can attached to multiple tasks. It is only possible to update
or delete a category with the correct id, this done to avoid
accidental deletions, for example typos, from taking place. The
natural flow, then, is to first fetch the correct category from the
REST API, extract the id and then modify it.

*Example:*
```bash
> TASK_ID=$(curl localhost:8080/v1/categories/name/CERN | jq .id)
> curl -X DELETE localhost:8080/v1/categories/$TASK_ID
```

### Create Category
- **URL:** `/v1/categories`
- **Method:** POST
- **Input:** JSON object describing the category. Must contain a name. 
- **Output:** JSON representation of the entered category. 
  
### View categories
- **URL:** `/v1/categories`
- **Method:** GET
- **Input:** None
- **Output:** List of all categories

### Get category by id
- **URL:** `/v1/categories/{id}`
- **Method:** GET 
- **Input:** None
- **Output:** Requested category

### Get category by name
- **URL:** `/v1/categories/name/{name}`
- **Method:** GET
- **Input:** None
- **Output:** Category matching that name. 

### Delete category by id
- **URL:** `/v1/categories/{id}`
- **Method:** DELETE
- **Input:** -
- **Output:** Successfully deleted category: {id}

### Update category by id
- **URL:** `/v1/categories`
- **Method:** PUT
- **Input:** JSON object of the category containing at least an id. 
- **Output:** The modified object.

## Tasks
Tasks are objects with a set deadline that can be tagged by a singular
categories. For similar reasons above, and the database model, a task
is identified in the API purely by the ID unless it is fetched from
the database. 

### Get tasks from database
- **URL:** `/v1/tasks`
- **Method:** GET
- **Input:** 
  - **Name:** a string that is used to filter the tasks by taskName.
  - **Sort:** indicates if tasks are to be sorted by _time_ or _name_.
  - **Direction:** are tasks sorted in _ascending_ or _descending_ order.
  - **Category:** filter task by category name
- **Ouput:** A list of tasks that, by default, are sorted ascending
on time.

### Get task from database
- **URL:** `/v1/tasks/{id}`
- **Method:** GET
- **Input:** None
- **Output:** Task from the database

### Update task
- **URL:** `/v1/tasks`
- **Method:** PUT
- **Input:** JSON task object cotnaing at least an id
- **Output:** Updated task

### Add task
- **URL:** `/v1/tasks`
- **Method:** POST
- **Input:** JSON task object containing at least a category with id. 
- **Output:** The task object

### Get by deadline
- **URL:** `/v1/tasks/deadline/[when]`
- **Method:** GET
- **Input:**
  - **when:** do you want the tasks with the deadline is _before_ or _after_
  - **time:** what is the time you are comparing the deadline.
**Output***: List of JSON objects which are either before or after the
deadline.

# Static verification
This repository uses the following tools to ensure code quality is
maintained: `PMD` (static analysis), `Checkstyle` (stylechecker),
`Spotbug` (static analysis) & `Jacoco` (test coverage). These tools
can be run by using the following additional commands:

- `./gradlew test` - Run the test suite
- `./gradlew checkstyleMain` - Run the stylechecker configured to
  Google style guide
- `./gradlew pmdMain` - Check the code using PMD
- `./gradlew jacocoTestReport` - Generate a HTML report to see the
  current coverage by unit tests.
  

