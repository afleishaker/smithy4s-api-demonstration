resource "aws_dynamodb_table" "jobs" {
  name         = "jobs"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "organization"
  range_key     = "title"

  attribute {
    name = "organization"
    type = "S"
  }

  attribute {
    name = "title"
    type = "S"
  }
}