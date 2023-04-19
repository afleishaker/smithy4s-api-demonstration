# :hammer::arrow_right::link: smithy4s-api-demonstration

_An example of leveraging [smithy4s](https://github.com/disneystreaming/smithy4s) to describe and generate traits to use in API development with [http4s](https://github.com/http4s/http4s) and a CLI using [decline](https://github.com/bkirwi/decline). Using [Localstack](https://github.com/localstack) to interface to local DynamoDB, this demo yields a functional, [cats-effect](https://typelevel.org/cats-effect/)-backed Scala service._ 

### Smithy

[smithy4s](https://github.com/disneystreaming/smithy4s) implements Scala codegen off of specifications of [Smithy](https://smithy.io/2.0/index.html), an IDL by AWS. 

In this demo, we define a _JobsService_ to retrieve and persist job listings. Given the operations and structures in our spec, smithy4s provides Scala traits defining route methods for us to implement and wire into http4s.

### Architecture

To power the API we implement based on our spec above, this demo defines a singular DynamoDB table in Terraform to store our job listings.


### Usage
#### Pre-steps
1. Install [Terraform](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli) to execute the Terraform containing our DDB table
2. Install [terraform-local](https://pypi.org/project/terraform-local/) with pip to get a Terraform wrapper macro, `tflocal`, which points your Terraform commands to our Localstack
3. Install [awscli-local](https://pypi.org/project/awscli-local/) with pip to get an AWS CLI wrapper macro, `awslocal`, which points your AWS CLI commands to our Localstack

#### Stand-up
1. Stand up the containers with: `docker-compose up`
2. Apply your Terraform infrastructure to the Localstack container with:
```
tflocal init
tflocal apply --auto-approve
```
3. Start the API with: `sbt run`

#### Interacting with the API
Once stood up, the API is live at https://localhost:9000.
* smithy4s generates Swagger docs at https://localhost:9000/docs, which can be used to test GET or POST on the implemented routes
* This can also be accomplished with the smithy4s-generated CLI, accessible at: `sbt jobs-service`
* Finally, the docker-compose also contains dynamodb-admin for local DDB viewing, accessible at: http://localhost:8001/