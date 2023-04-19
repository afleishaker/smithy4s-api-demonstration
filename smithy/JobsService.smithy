namespace smithy4s.jobs

use alloy#simpleRestJson

@simpleRestJson
service JobsService {
    version: "1.0.0",
    operations: [AddJob, GetJobs]
}

@http(method: "POST", uri: "/jobs/job", code: 200)
operation AddJob {
    input: Job,
    output: Job
}

@http(method: "GET", uri: "/jobs", code: 200)
operation GetJobs {
    input: JobsRequest
    output: JobsResponse
}

structure JobsRequest {
    @httpQuery("organization")
    organization: String

    @httpQuery("title")
    title: String
}

structure JobsResponse {
    @required
    @httpPayload
    jobs: Jobs
}

list Jobs {
    member: Job
}

structure Job {
    @required
    organization: String

    @required
    title: String

    @required
    description: String
}