# Overview
Shows how to configure an AWS Lambda sending a SQS message using AWS PrivateLink

Why is this unusual/important ?

Normally one would need to open the firewall/security group for the Lambda to allow connecting to the AWS SQS service on the https port, so 0.0.0.0/0:443. 
But with AWS PrivateLink, one can restrict the Lambda to connect directly to the local AWS SQS service on the https port (without NAT), and have the network 
packets not go over the Internet, enabling the enforcement of the least privilege/access principal

Before running the deployment, make sure you understand what is going to happen and that there will be costs involved

# Requirements
- Java 17
- sam (tested with SAM CLI, version 1.97.0 on Mac OS X 11.7.10 x86_64)

# Build and Deploy
`sam build && sam deploy`

# Testing
Once the deployment has completed successfully (which will take a few minutes), the output will show an API Gateway URL.
Now run `curl -v "AWS API Gateway URL from output"`. Check the SQS Queue `SampleQueueForSamPrivateLinkSqsFunction` for a message with the word "blah".
This was tested on a AWS Free account with basically no resources

# Cleanup 
Do this to avoid any further AWS costs and delete all resources created by the deployment: `sam delete`

# Notes
1. Running the "build" and "deploy" will create a VPC with CidrBlock: `10.0.0.0/16`. If you already have one of these, you would need to tweak the `template.yaml`
2. See the `template.yaml` file for the creation of the VPC Endpoint (that enables AWS PrivateLink) and related resources
3. See the `HelloWorldFunction/src/main/java/helloworld/App.java` file for the sending of the SQS message

# Resources
1. [AWS SAM CLI](<https://aws.amazon.com/serverless/sam/>)
2. [AWS PrivateLink Concepts](<https://docs.aws.amazon.com/vpc/latest/privatelink/concepts.html>)
3. [Internet access for Lambda](<https://repost.aws/knowledge-center/internet-access-lambda-function>)
4. [AWS PrivateLink](<https://aws.amazon.com/privatelink/>)
5. [What is AWS PrivateLink](<https://docs.aws.amazon.com/vpc/latest/privatelink/what-is-privatelink.html>)
6. [AWS CloudFormation VPC Endpoint](<https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-ec2-vpcendpoint.html>)
7. [Serverless Policy Templates](<https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-policy-templates.html>)
