# reject-k8s-secrets

A (quick and dirty) POC/sample implementation of a custom pre-receive hook for Atlassian's Bitbucket server.

The hook rejects specific `Secret` declarations in K8s YAML files.

The actual magic happens in ...
* [MyPreReceiveRepositoryHook.java](src/main/java/com/foobar/reject_k8s_secrets/hook/MyPreReceiveRepositoryHook.java)  

and
 
* [YamlParser.java](src/main/java/com/foobar/reject_k8s_secrets/parser/YamlParser.java)

Developed with [Atlassian SDK 8.2.7](https://marketplace.atlassian.com/apps/1210993/atlassian-plugin-sdk-tgz).
