# CS 587: Computer Systems Security

## Course Project: Authentication and Authorization in Microservices Architecture

### Overview

Traditionally, user authentication and authorization in monolithic web applications are done by using a unique session identifier that is sent to the user after successful authentication and needs to be passed by her with every request to establish her identity. However, this approach does not work for microservice-based applications. Microservices may be distributed and may have multiple instances of the same service running for better load balancing and higher availability. This means that the subsequent request of the same user, who was authenticated earlier with server *X*, may be served by a different server *Y*, which would need to establish the user's identity again before servicing the request.

This project models a **Document Management** system which uses **Role-based Access** to allow users to *read and write labeled documents* according to their *clearance level*. The user interacts with the system using a **RESTful API**. This API is **stateless**, meaning no session data and application-state is maintained at the server-side. Instead, application-state is maintained at the client-side with the help of **Java Web Tokens (JWT)**. Since the state is maintained on the client-side and is sent to the server on each request, any application server hosting these services can serve the user's request after extracting the user details from the JWT token.

This project itself does not adhere to the microservices architecture, but it is easy to break it down into separate services and register them with a discovery service to convert it into a set of microservices.

### Document Manager &mdash; The Example Application

A **document** in the system is represented by the following JSON:

```json
{
	"id": 5,
	"name": "Document1.txt",
	"label": {
		"id": 1,
		"name": "Unclassified"
	},
	"contents": "These are the contents of a test document."
}
```

Each document can be classified with one of the following **labels**:

- Unclassified
- Confidential
- Secret
- TopSecret

Each label has a corresponding **read** and **write** permission associated with it. Additionally, **upgrade** and **downgrade** permissions are also defined for all the labels, with an exception of *Unclassified* (no *downgrade* permission) and *TopSecret* (no *upgrade* permission).

Furthermore, a **manageUsers** permission is defined for system admin to manage users.

```
P = {
	readUnclassified,
	writeUnclassified,
	upgradeUnclassified
	readConfidential,
	writeConfidential,
	downgradeConfidential,
	upgradeConfidential,
	readSecret,
	writeSecret,
	downgradeSecret,
	upgradeSecret,
	readTopSecret,
	writeTopSecret,
	downgradeTopSecret
}
```

Each label has two kinds of roles defined &mdash; **User** and **Admin**, with an exception of *Unclassified*, which does not have an *admin* role. Additionally, a **systemAdmin** role has been defined for managin users in the system.

```
R = {
	unclassifiedUser,
	confidentialUser,
	confidentialAdmin,
	secretUser,
	secretAdmin,
	topSecretUser,
	topSecretAdmin,
	systemAdmin
}
```

Any *user* can read all documents below her level, but can only write at his own level. Any *admin* can can *downgrade* a document one level down and can also *upgrade* a document one level above.

```
RP = {
	[unclassifiedUser, readUnclassified],
	[unclassifiedUser, writeUnclassified],
	[confidentialUser, readUnclassified],
	[confidentialUser, readConfidential],
	[confidentialUser, writeConfidential],
	[confidentialAdmin, upgradeUnclassified],
	[confidentialAdmin, downgradeConfidential],
	[secretUser, readUnclassified],
	[secretUser, readConfidential],
	[secretUser, readSecret],
	[secretUser, writeSecret],
	[secretAdmin, upgradeConfidential],
	[secretAdmin, downgradeSecret],
	[topSecretUser, readUnclassified],
	[topSecretUser, readConfidential],
	[topSecretUser, readSecret],
	[topSecretUser, readTopSecret],
	[topSecretUser, writeTopSecret],
	[topSecretAdmin, upgradeSecret],
	[topSecretAdmin, downgradeTopSecret],
	[systemAdmin, manageUsers]
}
```

The users in the system are identified using their *E-mail ID*. A `User` object in the Java application is defined as:

```java
User(String firstName, String lastName, String email, String password, Role... roles)
```

The following users are defined in the application for demo purposes. The assigned roles appear last on each line.

```java
var users = Arrays.asList(  
	new User("Unclassified", "User", "unclassified.user@email.com", password, unclassifiedUser),  
	new User("Confidential", "User", "confidential.user@email.com", password, confidentialUser),  
	new User("Confidential", "Admin", "confidential.admin@email.com", password, confidentialUser, confidentialAdmin),  
	new User("Secret", "User", "secret.user@email.com", password, secretUser),  
	new User("Secret", "Admin", "secret.admin@email.com", password, secretUser, secretAdmin),  
	new User("Top Secret", "User", "top_secret.user@email.com", password, topSecretUser),  
	new User("Top Secret", "Admin", "top_secret.admin@email.com", password, topSecretAdmin),  
	new User("System", "Admin", "system.admin@email.com", password, systemAdmin)  
);
```

All passwords are **salted** and one-way **hashed** using the **bcrypt** hashing function. For demo purposes, all demo users have been assigned the password **"test"**.

### Technologies Used

- [Java 11 JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Java Web Tokens (JWT)](https://jwt.io)
- [Lombok](https://projectlombok.org/)
- [Gradle Build Tool](https://gradle.org/)

### How to Run

1.  Extract the project
2. Open *terminal* and browse to the project directory
3. Build the project and generate the Jar file
	```bash
	./gradlew clean build bootJar
	```
4. Run the jar file using Java
	```bash
	java -jar build/libs/document-manager-0.0.1-SNAPSHOT.jar
	```
	Alternatively, run directly using Gradle
	```bash
	./gradlew bootRun
	```
5. Use a REST API client, like **[Postman](https://www.getpostman.com/)** or **[CURL](https://curl.haxx.se/)** to interact with the application
6. Press `Ctrl`+`C` to shut down the server when done

### API Endpoints

There are three endpoints defined for the demo application:

1. **`/auth/`:** For authenticating users and generating JWT tokens.
2. **`/users/`:** For managing users. Can only be accessed by users having the `manageUsers` permission.
3. **`/documents/`:** For creating, reading, updating, deleting, and downgrading/upgrading documents.

### Authentication Endpoint

Users needs to authenticate themselves before they are allowed to access any other API endpoint.

**Example:** *Login as a user with "Secret" security clearance.*

```bash
curl -X POST \
  http://localhost:8080/auth/ \
  -H 'Content-Type: application/json' \
  -d '{
	"username": "confidential.user@email.com",
	"password": "test"
}'
```

If the user is authenticated successfully, the `Authorization` header in the response will contain the JWT token, that should be provided as a `Bearer` token for all subsequent queries.

```
Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjb25maWRlbnRpYWwudXNlckBlbWFpbC5jb20iLCJhdXRob3JpdGllcyI6WyJjb25maWRlbnRpYWxVc2VyIiwicmVhZENvbmZpZGVudGlhbCIsInJlYWRVbmNsYXNzaWZpZWQiLCJ3cml0ZUNvbmZpZGVudGlhbCJdLCJpYXQiOjE1NzYyMjg1MDYsImV4cCI6MTU3NjMxNDkwNn0.qRfR1g5HRsZV_WyEKpSlobCH_e2MYt0_DaDUedbbkQlS4bYwF6RXKp6_2lNk76rAz4rv2BJ8wcpLsr316q18bg
```

This JWT token has the following data encoded in it:

```json
{
  "sub": "confidential.user@email.com",
  "authorities": [
    "confidentialUser",
    "readConfidential",
    "readUnclassified",
    "writeConfidential"
  ],
  "iat": 1576228506,
  "exp": 1576314906
}
```

This JWT token is signed using HMACSHA512 algorithm. The secret key is only known to the authentication service, which validates that the tokens have not been tampered with.

### Users Endpoint

The `/users/` endpoint allows System Admins with `manageUsers` permission to *create*, *list*, *modify*, and *delete* users from the application. System Admins can also assign *roles* to users.

***Example:** List all users (after authenticating as a system admin user)*

```bash
curl -X GET \
  http://localhost:8080/users/ \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzeXN0ZW0uYWRtaW5AZW1haWwuY29tIiwiYXV0aG9yaXRpZXMiOlsibWFuYWdlVXNlcnMiLCJzeXN0ZW1BZG1pbiJdLCJpYXQiOjE1NzYyMjkzNjcsImV4cCI6MTU3NjMxNTc2N30.a0KeBL-ZtDcH9U9M9ldb5np6n0x_servsRoxEgxrl7GT2vXh8S7mHuvs1FD0PxSpnagMSNocueLXXq5id7L6Aw'
  
```

Response:

```json
[
    {
        "id": 17,
        "firstName": "Unclassified",
        "lastName": "User",
        "email": "unclassified.user@email.com",
        "password": "$2a$10$uSf1wK1Ian2twRJOu4Qkcegi5y4Nlo1H0kssa6yFiugtsfT8F9B4e",
        "roles": [
            {
                "id": "unclassifiedUser",
                "permissions": [
                    {
                        "id": "readUnclassified"
                    },
                    {
                        "id": "writeUnclassified"
                    }
                ]
            }
        ],
        "permissions": [
            {
                "id": "writeUnclassified"
            },
            {
                "id": "readUnclassified"
            }
        ],
        "fullName": "Unclassified User"
    },
    {
        "id": 18,
        "firstName": "Confidential",
        "lastName": "User",
        "email": "confidential.user@email.com",
        "password": "$2a$10$uSf1wK1Ian2twRJOu4Qkcegi5y4Nlo1H0kssa6yFiugtsfT8F9B4e",
        "roles": [
            {
                "id": "confidentialUser",
                "permissions": [
                    {
                        "id": "readConfidential"
                    },
                    {
                        "id": "readUnclassified"
                    },
                    {
                        "id": "writeConfidential"
                    }
                ]
            }
        ],
        "permissions": [
            {
                "id": "readUnclassified"
            },
            {
                "id": "writeConfidential"
            },
            {
                "id": "readConfidential"
            }
        ],
        "fullName": "Confidential User"
    },
    {
        "id": 19,
        "firstName": "Confidential",
        "lastName": "Admin",
        "email": "confidential.admin@email.com",
        "password": "$2a$10$uSf1wK1Ian2twRJOu4Qkcegi5y4Nlo1H0kssa6yFiugtsfT8F9B4e",
        "roles": [
            {
                "id": "confidentialUser",
                "permissions": [
                    {
                        "id": "readConfidential"
                    },
                    {
                        "id": "readUnclassified"
                    },
                    {
                        "id": "writeConfidential"
                    }
                ]
            },
            {
                "id": "confidentialAdmin",
                "permissions": [
                    {
                        "id": "downgradeConfidential"
                    },
                    {
                        "id": "upgradeUnclassified"
                    }
                ]
            }
        ],
        "permissions": [
            {
                "id": "upgradeUnclassified"
            },
            {
                "id": "writeConfidential"
            },
            {
                "id": "downgradeConfidential"
            },
            {
                "id": "readUnclassified"
            },
            {
                "id": "readConfidential"
            }
        ],
        "fullName": "Confidential Admin"
    },
    {
        "id": 20,
        "firstName": "Secret",
        "lastName": "User",
        "email": "secret.user@email.com",
        "password": "$2a$10$uSf1wK1Ian2twRJOu4Qkcegi5y4Nlo1H0kssa6yFiugtsfT8F9B4e",
        "roles": [
            {
                "id": "secretUser",
                "permissions": [
                    {
                        "id": "readConfidential"
                    },
                    {
                        "id": "readSecret"
                    },
                    {
                        "id": "readUnclassified"
                    },
                    {
                        "id": "writeSecret"
                    }
                ]
            }
        ],
        "permissions": [
            {
                "id": "readUnclassified"
            },
            {
                "id": "readConfidential"
            },
            {
                "id": "writeSecret"
            },
            {
                "id": "readSecret"
            }
        ],
        "fullName": "Secret User"
    },
    {
        "id": 21,
        "firstName": "Secret",
        "lastName": "Admin",
        "email": "secret.admin@email.com",
        "password": "$2a$10$uSf1wK1Ian2twRJOu4Qkcegi5y4Nlo1H0kssa6yFiugtsfT8F9B4e",
        "roles": [
            {
                "id": "secretUser",
                "permissions": [
                    {
                        "id": "readConfidential"
                    },
                    {
                        "id": "readSecret"
                    },
                    {
                        "id": "readUnclassified"
                    },
                    {
                        "id": "writeSecret"
                    }
                ]
            },
            {
                "id": "secretAdmin",
                "permissions": [
                    {
                        "id": "downgradeSecret"
                    },
                    {
                        "id": "upgradeConfidential"
                    }
                ]
            }
        ],
        "permissions": [
            {
                "id": "upgradeConfidential"
            },
            {
                "id": "writeSecret"
            },
            {
                "id": "readUnclassified"
            },
            {
                "id": "readConfidential"
            },
            {
                "id": "readSecret"
            },
            {
                "id": "downgradeSecret"
            }
        ],
        "fullName": "Secret Admin"
    },
    {
        "id": 22,
        "firstName": "Top Secret",
        "lastName": "User",
        "email": "top_secret.user@email.com",
        "password": "$2a$10$uSf1wK1Ian2twRJOu4Qkcegi5y4Nlo1H0kssa6yFiugtsfT8F9B4e",
        "roles": [
            {
                "id": "topSecretUser",
                "permissions": [
                    {
                        "id": "readConfidential"
                    },
                    {
                        "id": "readSecret"
                    },
                    {
                        "id": "readTopSecret"
                    },
                    {
                        "id": "readUnclassified"
                    },
                    {
                        "id": "writeTopSecret"
                    }
                ]
            }
        ],
        "permissions": [
            {
                "id": "writeTopSecret"
            },
            {
                "id": "readTopSecret"
            },
            {
                "id": "readSecret"
            },
            {
                "id": "readUnclassified"
            },
            {
                "id": "readConfidential"
            }
        ],
        "fullName": "Top Secret User"
    },
    {
        "id": 23,
        "firstName": "Top Secret",
        "lastName": "Admin",
        "email": "top_secret.admin@email.com",
        "password": "$2a$10$uSf1wK1Ian2twRJOu4Qkcegi5y4Nlo1H0kssa6yFiugtsfT8F9B4e",
        "roles": [
            {
                "id": "topSecretAdmin",
                "permissions": [
                    {
                        "id": "downgradeTopSecret"
                    },
                    {
                        "id": "upgradeSecret"
                    }
                ]
            }
        ],
        "permissions": [
            {
                "id": "downgradeTopSecret"
            },
            {
                "id": "upgradeSecret"
            }
        ],
        "fullName": "Top Secret Admin"
    },
    {
        "id": 24,
        "firstName": "System",
        "lastName": "Admin",
        "email": "system.admin@email.com",
        "password": "$2a$10$uSf1wK1Ian2twRJOu4Qkcegi5y4Nlo1H0kssa6yFiugtsfT8F9B4e",
        "roles": [
            {
                "id": "systemAdmin",
                "permissions": [
                    {
                        "id": "manageUsers"
                    }
                ]
            }
        ],
        "permissions": [
            {
                "id": "manageUsers"
            }
        ],
        "fullName": "System Admin"
    }
]
```

### Documents Endpoint

The `/documents/` endpoint can be used to *create*, *read*, *list*, *update*, *rename*, *downgrade*, and *upgrade* documents.

***Example:** Listing all documents which can be read by the user.*

```bash
curl -X GET \
  http://localhost:8080/documents/ \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzZWNyZXQudXNlckBlbWFpbC5jb20iLCJhdXRob3JpdGllcyI6WyJyZWFkQ29uZmlkZW50aWFsIiwicmVhZFNlY3JldCIsInJlYWRVbmNsYXNzaWZpZWQiLCJzZWNyZXRVc2VyIiwid3JpdGVTZWNyZXQiXSwiaWF0IjoxNTc2MjMwMTQ0LCJleHAiOjE1NzYzMTY1NDR9.1xkxklGauqqzmiPu_JenDYMRhh0EoEDcIeDQHxuO_NFbTL3DGgjN_nxyI0bwMxwqzzG-fINSN2ptk9dl8IlZyA'
```

Response:

```json
[
    {
        "id": 5,
        "name": "Document1.txt",
        "label": {
            "id": 1,
            "name": "Unclassified"
        },
        "contents": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Sapien faucibus et molestie ac feugiat sed lectus. Dictumst quisque sagittis purus sit amet. Mauris augue neque gravida in fermentum. Pretium lectus quam id leo in vitae turpis. Pellentesque massa placerat duis ultricies lacus sed turpis tincidunt. Vitae tempus quam pellentesque nec nam aliquam sem. Leo in vitae turpis massa sed elementum. Nulla porttitor massa id neque aliquam vestibulum morbi blandit. Maecenas accumsan lacus vel facilisis volutpat est velit egestas. Tincidunt augue interdum velit euismod in pellentesque massa placerat duis. Lectus magna fringilla urna porttitor. Fusce ut placerat orci nulla pellentesque dignissim enim sit. Tellus elementum sagittis vitae et leo duis ut diam. Consectetur lorem donec massa sapien."
    },
    {
        "id": 6,
        "name": "Document2.txt",
        "label": {
            "id": 2,
            "name": "Confidential"
        },
        "contents": "Mi sit amet mauris commodo quis imperdiet massa tincidunt. Urna nunc id cursus metus aliquam eleifend mi in nulla. Nibh tellus molestie nunc non blandit massa enim. Sagittis purus sit amet volutpat consequat mauris. Vivamus arcu felis bibendum ut tristique et egestas. Cursus risus at ultrices mi tempus imperdiet nulla malesuada pellentesque. Ultrices gravida dictum fusce ut placerat orci nulla. Nulla malesuada pellentesque elit eget gravida cum. Nec feugiat nisl pretium fusce id velit ut tortor pretium. Rhoncus dolor purus non enim praesent elementum facilisis leo vel. Ullamcorper eget nulla facilisi etiam. Gravida quis blandit turpis cursus in hac habitasse platea. Proin fermentum leo vel orci porta non pulvinar. Neque vitae tempus quam pellentesque nec nam. Fermentum dui faucibus in ornare quam viverra orci sagittis."
    },
    {
        "id": 7,
        "name": "Document3.txt",
        "label": {
            "id": 3,
            "name": "Secret"
        },
        "contents": "Lobortis scelerisque fermentum dui faucibus in ornare quam viverra orci. Sagittis vitae et leo duis ut diam quam nulla porttitor. Aliquet nec ullamcorper sit amet risus nullam eget felis. Leo vel orci porta non. Sed libero enim sed faucibus turpis in eu. Neque vitae tempus quam pellentesque. Diam in arcu cursus euismod quis viverra nibh. Duis ultricies lacus sed turpis tincidunt id. Egestas pretium aenean pharetra magna ac placerat vestibulum. Penatibus et magnis dis parturient. Leo in vitae turpis massa sed elementum tempus. Arcu felis bibendum ut tristique et egestas quis ipsum suspendisse. Mi ipsum faucibus vitae aliquet nec. Laoreet sit amet cursus sit amet dictum sit amet justo."
    },
    {
        "id": 9,
        "name": "Document5.txt",
        "label": {
            "id": 1,
            "name": "Unclassified"
        },
        "contents": "In massa tempor nec feugiat nisl pretium fusce id velit. Egestas sed sed risus pretium quam vulputate dignissim suspendisse in. At risus viverra adipiscing at in tellus. Porta nibh venenatis cras sed. Tempor id eu nisl nunc. Commodo elit at imperdiet dui accumsan sit. Ullamcorper eget nulla facilisi etiam. Quam nulla porttitor massa id neque aliquam. Quisque non tellus orci ac auctor. Nunc non blandit massa enim nec dui. Condimentum lacinia quis vel eros donec."
    },
    {
        "id": 10,
        "name": "Document6.txt",
        "label": {
            "id": 2,
            "name": "Confidential"
        },
        "contents": "Quam viverra orci sagittis eu volutpat odio facilisis. Mauris cursus mattis molestie a iaculis at erat pellentesque adipiscing. Dui accumsan sit amet nulla facilisi. Facilisis leo vel fringilla est ullamcorper eget nulla facilisi etiam. Tristique senectus et netus et malesuada fames ac turpis egestas. Donec adipiscing tristique risus nec feugiat in fermentum. Etiam erat velit scelerisque in dictum non consectetur. Aenean sed adipiscing diam donec. Aenean pharetra magna ac placerat vestibulum lectus mauris. Molestie a iaculis at erat pellentesque adipiscing. Interdum posuere lorem ipsum dolor sit. Facilisi morbi tempus iaculis urna id volutpat lacus laoreet non. Ac felis donec et odio pellentesque diam. Sociis natoque penatibus et magnis dis. Morbi enim nunc faucibus a pellentesque sit amet. Tellus in hac habitasse platea dictumst vestibulum rhoncus. Accumsan lacus vel facilisis volutpat est velit egestas dui id. Ultricies mi eget mauris pharetra et ultrices neque."
    },
    {
        "id": 11,
        "name": "Document7.txt",
        "label": {
            "id": 3,
            "name": "Secret"
        },
        "contents": "Ipsum dolor sit amet consectetur. Tellus elementum sagittis vitae et. Eu sem integer vitae justo eget magna fermentum. Eget magna fermentum iaculis eu non diam phasellus vestibulum. Nisl suscipit adipiscing bibendum est ultricies integer. Habitant morbi tristique senectus et netus et malesuada. Ornare quam viverra orci sagittis eu volutpat odio. Enim eu turpis egestas pretium aenean. Elit ut aliquam purus sit amet. Diam maecenas sed enim ut. Felis imperdiet proin fermentum leo vel orci. Lacus vel facilisis volutpat est velit egestas dui id ornare. Fames ac turpis egestas integer eget aliquet nibh praesent. Aliquet bibendum enim facilisis gravida neque convallis a cras semper. Pretium quam vulputate dignissim suspendisse. A scelerisque purus semper eget duis at tellus."
    },
    {
        "id": 13,
        "name": "Document9.txt",
        "label": {
            "id": 1,
            "name": "Unclassified"
        },
        "contents": "Lacus vestibulum sed arcu non. Consectetur purus ut faucibus pulvinar elementum. Turpis in eu mi bibendum. Ullamcorper morbi tincidunt ornare massa eget egestas purus viverra. Enim ut sem viverra aliquet eget sit amet tellus cras. Ut enim blandit volutpat maecenas volutpat blandit aliquam. Aliquam eleifend mi in nulla posuere sollicitudin aliquam. Molestie nunc non blandit massa. Mi sit amet mauris commodo quis imperdiet massa tincidunt nunc. Tincidunt dui ut ornare lectus sit amet est placerat in. Ultrices tincidunt arcu non sodales neque. Convallis tellus id interdum velit laoreet id donec ultrices. Risus feugiat in ante metus. Nisi scelerisque eu ultrices vitae auctor eu augue. At imperdiet dui accumsan sit. Non sodales neque sodales ut. Elit ut aliquam purus sit amet luctus venenatis lectus magna."
    },
    {
        "id": 14,
        "name": "Document10.txt",
        "label": {
            "id": 2,
            "name": "Confidential"
        },
        "contents": "In cursus turpis massa tincidunt dui ut ornare. Donec pretium vulputate sapien nec sagittis aliquam malesuada. Massa sed elementum tempus egestas sed sed. Et tortor at risus viverra adipiscing at in. Cursus risus at ultrices mi tempus imperdiet nulla malesuada. Sed faucibus turpis in eu mi bibendum neque egestas. Mauris pharetra et ultrices neque ornare aenean euismod elementum. Etiam non quam lacus suspendisse faucibus interdum posuere lorem ipsum. Et malesuada fames ac turpis egestas maecenas pharetra. Id venenatis a condimentum vitae sapien pellentesque. Interdum consectetur libero id faucibus. Nulla facilisi morbi tempus iaculis urna id volutpat. Ut etiam sit amet nisl purus in. Imperdiet nulla malesuada pellentesque elit."
    },
    {
        "id": 15,
        "name": "Document11.txt",
        "label": {
            "id": 3,
            "name": "Secret"
        },
        "contents": "Pharetra magna ac placerat vestibulum lectus. Tellus pellentesque eu tincidunt tortor aliquam nulla facilisi cras fermentum. Interdum varius sit amet mattis vulputate enim nulla aliquet porttitor. Tincidunt praesent semper feugiat nibh sed pulvinar proin. Sollicitudin ac orci phasellus egestas tellus rutrum tellus pellentesque eu. Aliquam nulla facilisi cras fermentum. Tellus molestie nunc non blandit massa enim. Semper feugiat nibh sed pulvinar. Aliquet risus feugiat in ante metus dictum at tempor. Nisi quis eleifend quam adipiscing vitae proin sagittis. Cras adipiscing enim eu turpis egestas pretium aenean pharetra. Gravida quis blandit turpis cursus in hac habitasse platea. Neque vitae tempus quam pellentesque. Ipsum consequat nisl vel pretium lectus quam id leo. Lobortis elementum nibh tellus molestie nunc."
    }
]
```

***Example:** Get a specific document by its ID.*

```bash
curl -X GET \
  http://localhost:8080/documents/15 \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzZWNyZXQudXNlckBlbWFpbC5jb20iLCJhdXRob3JpdGllcyI6WyJyZWFkQ29uZmlkZW50aWFsIiwicmVhZFNlY3JldCIsInJlYWRVbmNsYXNzaWZpZWQiLCJzZWNyZXRVc2VyIiwid3JpdGVTZWNyZXQiXSwiaWF0IjoxNTc2MjMwMTQ0LCJleHAiOjE1NzYzMTY1NDR9.1xkxklGauqqzmiPu_JenDYMRhh0EoEDcIeDQHxuO_NFbTL3DGgjN_nxyI0bwMxwqzzG-fINSN2ptk9dl8IlZyA'
```

Response:

```json
{
    "id": 15,
    "name": "Document11.txt",
    "label": {
        "id": 3,
        "name": "Secret"
    },
    "contents": "Pharetra magna ac placerat vestibulum lectus. Tellus pellentesque eu tincidunt tortor aliquam nulla facilisi cras fermentum. Interdum varius sit amet mattis vulputate enim nulla aliquet porttitor. Tincidunt praesent semper feugiat nibh sed pulvinar proin. Sollicitudin ac orci phasellus egestas tellus rutrum tellus pellentesque eu. Aliquam nulla facilisi cras fermentum. Tellus molestie nunc non blandit massa enim. Semper feugiat nibh sed pulvinar. Aliquet risus feugiat in ante metus dictum at tempor. Nisi quis eleifend quam adipiscing vitae proin sagittis. Cras adipiscing enim eu turpis egestas pretium aenean pharetra. Gravida quis blandit turpis cursus in hac habitasse platea. Neque vitae tempus quam pellentesque. Ipsum consequat nisl vel pretium lectus quam id leo. Lobortis elementum nibh tellus molestie nunc."
}
```

***Example:** Get a document by its name.*

```bash
curl -X GET \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzZWNyZXQudXNlckBlbWFpbC5jb20iLCJhdXRob3JpdGllcyI6WyJyZWFkQ29uZmlkZW50aWFsIiwicmVhZFNlY3JldCIsInJlYWRVbmNsYXNzaWZpZWQiLCJzZWNyZXRVc2VyIiwid3JpdGVTZWNyZXQiXSwiaWF0IjoxNTc2MjMwMTQ0LCJleHAiOjE1NzYzMTY1NDR9.1xkxklGauqqzmiPu_JenDYMRhh0EoEDcIeDQHxuO_NFbTL3DGgjN_nxyI0bwMxwqzzG-fINSN2ptk9dl8IlZyA'
```

Response:

```json
{
    "id": 14,
    "name": "Document10.txt",
    "label": {
        "id": 2,
        "name": "Confidential"
    },
    "contents": "In cursus turpis massa tincidunt dui ut ornare. Donec pretium vulputate sapien nec sagittis aliquam malesuada. Massa sed elementum tempus egestas sed sed. Et tortor at risus viverra adipiscing at in. Cursus risus at ultrices mi tempus imperdiet nulla malesuada. Sed faucibus turpis in eu mi bibendum neque egestas. Mauris pharetra et ultrices neque ornare aenean euismod elementum. Etiam non quam lacus suspendisse faucibus interdum posuere lorem ipsum. Et malesuada fames ac turpis egestas maecenas pharetra. Id venenatis a condimentum vitae sapien pellentesque. Interdum consectetur libero id faucibus. Nulla facilisi morbi tempus iaculis urna id volutpat. Ut etiam sit amet nisl purus in. Imperdiet nulla malesuada pellentesque elit."
}
```

***Example:** Create a new document.*

```bash
curl -X POST \
  http://localhost:8080/documents/ \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzZWNyZXQudXNlckBlbWFpbC5jb20iLCJhdXRob3JpdGllcyI6WyJyZWFkQ29uZmlkZW50aWFsIiwicmVhZFNlY3JldCIsInJlYWRVbmNsYXNzaWZpZWQiLCJzZWNyZXRVc2VyIiwid3JpdGVTZWNyZXQiXSwiaWF0IjoxNTc2MjMwMTQ0LCJleHAiOjE1NzYzMTY1NDR9.1xkxklGauqqzmiPu_JenDYMRhh0EoEDcIeDQHxuO_NFbTL3DGgjN_nxyI0bwMxwqzzG-fINSN2ptk9dl8IlZyA' \
  -H 'Content-Type: application/json' \
  -d '{
	"name": "New Test Document",
	"label": {
		"name": "Secret"
	},
	"contents": "This is a new test document."
}'
```

Response:

```json
{
    "id": 25,
    "name": "New Test Document",
    "label": {
        "id": 3,
        "name": "Secret"
    },
    "contents": "This is a new test document."
}
```

***Example:** Downgrade a document.*

```bash
curl -X PUT \
  http://localhost:8080/documents/25/downgrade \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzZWNyZXQuYWRtaW5AZW1haWwuY29tIiwiYXV0aG9yaXRpZXMiOlsiZG93bmdyYWRlU2VjcmV0IiwicmVhZENvbmZpZGVudGlhbCIsInJlYWRTZWNyZXQiLCJyZWFkVW5jbGFzc2lmaWVkIiwic2VjcmV0QWRtaW4iLCJzZWNyZXRVc2VyIiwidXBncmFkZUNvbmZpZGVudGlhbCIsIndyaXRlU2VjcmV0Il0sImlhdCI6MTU3NjIzMTQ5OSwiZXhwIjoxNTc2MzE3ODk5fQ.MYSBopu02sBIEYbVVF05csnE9vNbK79KK2NfqqOEiH_9t1TAkoPDbEEtxXsobd09y8c-Yn2Hf_Z2rKxVboZ2og' \
```

Response:

```json
{
    "id": 25,
    "name": "New Test Document",
    "label": {
        "id": 2,
        "name": "Confidential"
    },
    "contents": "This is a new test document."
}
```

### References

- [Spring Security for a REST API | Baeldung](https://www.baeldung.com/securing-a-restful-web-service-with-spring-security)
- [Spring Security - Roles and Privileges | Baeldung](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)
- [Getting Started · Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
- [Microservices with Spring Boot — Authentication with JWT (Part 3)](https://medium.com/omarelgabrys-blog/microservices-with-spring-boot-authentication-with-jwt-part-3-fafc9d7187e8)
- [Lorem Ipsum Generator](https://loremipsum.io/generator/?n=12&t=p)
