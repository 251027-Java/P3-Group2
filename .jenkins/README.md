# Jenkins

This guide covers how to set up Jenkins and use it as a developer.

## Prequisites

- [Docker](https://www.docker.com/get-started/)
- [Just](https://just.systems/man/en/introduction.html)
  
  An easy way to install Just is using `npm`:

  ```sh
  npm i -g rust-just
  just --version # verify that Just was installed
  ```

  Just can be installed through other [package managers](https://just.systems/man/en/packages.html) and is also available as [pre-built binaries](https://just.systems/man/en/pre-built-binaries.html).

## Using `just`

`just` commands/recipes are defined in the [justfile](./justfile). 

To view all available recipes:

```sh
just -l
```

To run commands, you'll need a `.env` file. See [this section](#initialization) for more information.

## Features

| Feature                               | Description                                                                                                                                                                      |
| ------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Only-necessary execution              | The pipeline only runs on pushes to `main` and PRs targetting `main`. Furthermore, if there are no changes to the service's directory, the pipeline will not perform a full run. |
| Pipeline file configuration           | Services can configure their pipeline run through `.ci.json` at the base of their directory.                                                                                     |
| Service autodetection                 | The pipeline will automatically detect directories with `.ci.json` files and run accordingly.                                                                                    |
| Pipeline alteration via attributes    | Pipeline behavior can be altered through "attributes" or special strings in commit messages to bypass default behavior.                                                          |
| GitHub checks and commit statuses     | Pipeline status, such as success/failure, can be viewed on GitHub per commit.                                                                                                    |
| Automatic Docker Hub image publishing | Images are automatically built upon pushes to the `main` branch.                                                                                                                 |
| Multi-platform image build support    | Configuration for building multi-platform images using [native nodes](https://docs.docker.com/build/building/multi-platform/#multiple-native-nodes) via SSH is available.        |

### Pipeline file configuration

Service pipelines can be configured through a file called `.ci.json` where you can dictate what stages should run, the command to execute, and image publishing configuration. 

- [Schema](./ci.schema.json)
- [Example file](./.ci.json.example)

#### VSCode schema integration

To add intellisense and validation for `.ci.json`, the schema can be added to your settings or specified within the file itself.

**Within a file**

```jsonc
{
  "$schema": "../../.jenkins/ci.schema.json", // can be a url or a relative path to the schema
  // ...
}
```

**Workspace settings**

An alternative way to add intellisense and validation without using `$schema` is to specify it in your workspace settings.

```jsonc
{
  "json.schemas": [{
    "fileMatch": ["**/*.ci.json"],
    "url": "/.jenkins/ci.schema.json"
  }]
}
```

View VSCode's [official documentation](https://code.visualstudio.com/Docs/languages/json#_json-schemas-and-settings) for more information.

### Pipeline alteration via attributes

Attributes are special strings provided in your commit messages to bypass the default behavior of the pipeline. To specify an attribute or multiple, enclose them with square brackets and separate them with commas.

**Examples**

| Attributes                      | Description                                                                                                                                      |
| ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| `[backend,run]`                 | Forces all backend services to run the pipeline.                                                                                                 |
| `[latest]`                      | Push a `latest` image variant if an image is built.                                                                                              |
| `[change:backend/user-service]` | Simulate a change occurred in the `backend/user-service/` directory and run the pipeline against the service located at `backend/user-service/`. |

Our Jenkins pipeline is set up to read the last enclosing square brackets in the commit message and parse them as attributes.

**Examples**

```sh
git commit -m "a summary" -m "[backend,run]" # Parses [backend,run]
git commit -m "a summary" -m "[backend,run] [frontend]" # Parses [frontend]
git commit -m "a summary [backend,run]" -m "some content [frontend]" # Parses [frontend]
git commit -m "a summary [backend,run]" -m "some [pr:default] content" # Parses [pr:default]
git commit -m "a [pr:default] summary" -m "some content" # Parses [pr:default]
git commit -m "a [pr:default] summary" # Parses [pr:default]
```

#### Supported attributes

|   Attributes   |           Example            | Description                                                                                                              |
| :------------: | :--------------------------: | ------------------------------------------------------------------------------------------------------------------------ |
|     `skip`     |              -               | Skips testing-related stages. This includes the `lint`, `test`, and `build` stages.                                      |
|     `run`      |              -               | Force all testing-related stages to run. Use in tandem with `frontend` and/or `backend` to depict which services to run. |
|   `frontend`   |              -               | Enters the `frontend` stage and check the `frontend/` directory.                                                         |
|   `backend`    |              -               | Enters the `backend` stage and check the `backend/` directory.                                                           |
|   `default`    |              -               | Interprets the commit as if on the default branch. Allows for pushing `latest` image variants if images are built.       |
|    `latest`    |              -               | Pushes `latest` image variants if images are built.                                                                      |
|  `pr:default`  |              -               | Interprets the commit as if on a PR targetting the default branch.                                                       |
|   `imageall`   |              -               | Build and pushes all images.                                                                                             |
| `build:<path>` | `build:backend/user-service` | Indicates a success build for the given service. Allows for an image to be built.                                        |
| `image:<path>` | `image:backend/user-service` | Build an image for the given service.                                                                                    |

#### Usage examples

|                Attributes                 | Description                                                                                                                                                                   |
| :---------------------------------------: | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|          `backend,frontend,run`           | Run the entire pipeline against the `frontend/` and `backend/` directory.                                                                                                     |
|        `frontend,imageall,latest`         | Build and push images for the `frontend/` directory with `latest` variants.                                                                                                   |
| `backend,skip,image:backend/user-service` | Skips all other test-related stages that may be triggered from the original commit's changes, enters the backend stage, and builds an image for the `user-service` directory. |

Attributes are particularly useful for triggering behavior with empty commits:

```sh
git commit -m "build backend images" -m "[backend,imageall]" --allow-empty
```

### GitHub checks and commit statuses

We make use of [GitHub Apps](https://docs.github.com/en/apps/creating-github-apps/registering-a-github-app/registering-a-github-app) to handle authentication and utilization of GitHub's [Checks API](https://docs.github.com/en/rest/checks?apiVersion=2022-11-28). 

Create your GitHub App under your account or organization and add it to your repository. Ensure the following permissions are selected with the appropriate access:

| Permission                   | Access         |
| ---------------------------- | -------------- |
| Repository > Checks          | Read and write |
| Repository > Commit statuses | Read and write |
| Repository > Contents        | Read-only      |
| Repository > Pull requests   | Read-only      |

Although a bit dated, view the [guide](https://github.com/jenkinsci/github-branch-source-plugin/blob/master/docs/github-app.adoc) on incorporating GitHub Apps with Jenkins.

After configuring your GitHub App, the `App ID` should be specified in `secrets/secrets.properties`. Additionally, a private key should be generated from GitHub and then converted to a format that Jenkins can read. Take the generated key and use the `just` command to convert it:

```sh
just convert-key <the-original-github-key-file> secrets/github_app_team_key.pem

# Example
just convert-key some-github-app-name.2026-01-10.private-key.pem secrets/github_app_team_key.pem
```

The key used by Jenkins should be called `github_app_team_key.pem` and in the `secrets/` directory.

### Automatic Docker Hub image publishing

To publish images to Docker Hub, create a [Personal Access Token (PAT)](https://docs.docker.com/security/access-tokens/#create-a-personal-access-token) with the `Read & Write` permissions. Your username and PAT should be specified in the `secrets/secrets.properties` file.

### Multi-platform image build support

Multi-platform builds are done using the [native node](https://docs.docker.com/build/building/multi-platform/#multiple-native-nodes) strategy. This is an **OPTIONAL** feature and not necessary to execute Jenkins. If not you choose not to set up this configuration, you'll be limited to the CPU archiecture of your host system. Use `just docker-arch` to see what architecture your builds will natively package for.

SSH is used to handle connections with nodes/other VMs. An SSH key can be generated with the following command:

```sh
just gen-docker-ssh-key
```

This will generate a passwordless SSH key called `docker-ssh-key` in the `ssh/` directory. It is your responsibility to make sure the public key contents are added to your nodes' `~/.ssh/authorized_keys` file and that your nodes have Docker already set up.

To finish configuration, create the SSH Config file at `ssh/config`. Create the Host blocks necessary for your nodes. These should all have a similar structure to the following:

```sshconfig
Host <platform>
  HostName <ip>
  User <username>
  IdentifyFile ~/.ssh/docker-ssh-key

# Example
Host linux-arm64
  HostName 1.1.1.1
  User ubuntu
  IdentifyFile ~/.ssh/docker-ssh-key
```

The `<platform>` should be named after the platform that your node natively builds to. Effectively, it is the corresponding docker platform but the `/` character is replaced with `-`. Run the following command on your nodes to obtain the pattern to use in your config file:

```sh
docker version --format '{{.Server.Os}}-{{.Server.Arch}}' | tr '/' '-'
```

## Initialization

There's some configuration involved to start up Jenkins. We use a `.env` and secrets in the `secrets/` directory to contain that configuration.

Use the following commands to create the necessary files automatically:

```sh
cp .env.example .env
just init-files
```

### `.env` Configuration

Some variables already have provided values. These values can be modified to better suit your configuration if necessary.

### Secrets Configuration

Provide values for the variables in the `secrets/secrets.properties` file.

See the following sections to help complete set up:

- [GitHub checks and commit statuses](#github-checks-and-commit-statuses)
- [Automatic Docker Hub image publishing](#automatic-docker-hub-image-publishing)

### SSH Configuration

If you're wanting to build multi-platform images with native nodes, see [Multi-platform image build support](#multi-platform-image-build-support).

### Creation

Once configuration has been set, the Jenkins server can be created:

```sh
just init-jenkins
```

This will create the Jenkins server with the necessary plugins installed, custom configurations set, and jobs created. After running this command, there will be additional instructions output to the terminal that should be completed to finish the set up, which will briefly be discussed in this guide. If you want to see those instructions at any point, use `just instructions`.

### Jenkins API Token and Jenkins CLI

Using the Jenkins CLI allows for remote use, scriptability, and interaction with the Jenkins server through the terminal. In order to send commands to a Jenkins server, you need an API token. A password could also be used rather than an API token, but for security management, it's recommended to use an API token as multiple can be made and deleted if necessary.

To create an API token for a user, go to `<jenkins-url>/user/<username>/security` in your browser where `<jenkins-url>` is the URL to the Jenkins server and `<username>` is the name of the user, e.g. `http://localhost:8080/user/admin/security`. After creating the token, copy the token, and set the `JENKINS_API_TOKEN` variable in the `.env` file to the token.

If you haven't downloaded the CLI, use `just dl`. Once you have the CLI, you should be able to use the Jenkins CLI through `just j`.

```sh
just j list-jobs
just j version
```

> [!NOTE]
> `just j` uses the `java` CLI, so ensure it's available on your `PATH`.

Use `just help` to view available commands, or in your browser, go to `<jenkins-url>/cli`. Substitute `<jenkins-url>` with the URL of your Jenkins server. 

At this point, Jenkins and your local environment should be fully set up. To understand how to manage configuration updates, see [Updating Configuration](#updating-configuration).

## Starting

Start Jenkins:

```sh
just start
```

## Stopping

Stop Jenkins:

```sh
just stop
```

## Destroying

To destroy all of Jenkins, including its data, run:

```sh
just destroy
```

## Updating Configuration

To manage Jenkins configuration, we use the [JCasC Plugin](https://github.com/jenkinsci/configuration-as-code-plugin) and the files are located [here](./configs/). The full configuration that Jenkins uses is actually a lot larger, but ours only contains our custom configurations that will override the default values.

If you make updates to the configuration, you'll likely first make updates through the UI. If you want to save those changes to the config files, you can view the current state of the JCasC file on the Jenkins server by using the following command:

```sh
just view-config
just v # Shorthand
```

Copy the modified parts and update `configs/dynamic.yml` with those changes.

### Static and Dynamic Configuration

We have two configuration files `static.yml` and `dynamic.yml`. 
- `static.yml` contains properties that **CANNOT** be remotely applied to the server via [remote application](#remote-application). 
- `dynamic.yml` contains properties that **CAN** be remotely applied to the server via [remote application](#remote-application).

How we identify which properties belong to which file is simply trial and error. By default, you should make your updates to `dynamic.yml`. If you can remotely apply it to the server, then it should stay there. Otherwise, move it to `static.yml`.

### Sending and Reloading Configuration

There's two ways to update Jenkins' configuration through the terminal. If we're on the same machine as the running Jenkins instance, we can simply modify the files directly as they're mounted through Docker Compose. Otherwise, we can send the config to the server remotely.

#### Local application

After modifying the local configuration files, we can reload the configuration.

```sh
just reload-config
just r # Shorthand
```

#### Remote application

We can send the server `configs/dynamic.yml` to update its configuration and reload it.

```sh
just apply-config
just a # Shorthand
```

> [!NOTE]
> This will only send and apply the `configs/dynamic.yml` file. If you find yourself running into errors involving conflicting values, it is likely a property that should belong to `configs/static.yml` and should be handled through [local application](#local-application). See [Static and Dynamic Configuration](#static-and-dynamic-configuration) for more information.

### Job Configuration

We use the [Job DSL Plugin](https://github.com/jenkinsci/job-dsl-plugin) to create jobs. When making changes to jobs through the UI, it's important to note that there is no way to obtain the job configuration in Job DSL API syntax. You have to manually write those changes in `configs/dynamic.yml`. Refer to the documentation on the Job DSL API by going to the [general site](https://jenkinsci.github.io/job-dsl-plugin) or for more specific information tailored to your plugins, `<jenkins-url>/plugin/job-dsl/api-viewer/index.html`. Additional information on working with Job DSL and JCasC together can be found [here](https://github.com/jenkinsci/job-dsl-plugin/wiki/JCasC).

After updating `configs/dynamic.yml`, ensure you update and reload the configuration on the Jenkins server:

```sh
just r
```

## Tunneling

If you're running Jenkins on a non-VPS system or a system that doesn't/shouldn't have its ports exposed, we can use a tunnel to redirect payloads to our server without having to expose our own ports. This allows us to make use of Jenkins' GitHub webhook integretaion.

To enable this, ensure the Jenkins server is running and then run:


```sh
just tunnel # Outputs a URL where we can access our Jenkins server
```
> [!NOTE]
> `just tunnel` calls `npx`, so ensure you have [Node.js](https://nodejs.org/en) available on your `PATH`.

This uses [localtunnel](https://github.com/localtunnel/localtunnel) to easily give us a way to send webhooks from GitHub to our server. Similar tools exist that could also be used, such as [ngrok](https://ngrok.com/). Some others can be found [here](https://github.com/anderspitman/awesome-tunneling).

Once you have the URL, your webhook URL that you enter in GitHub will look something like `https://weak-beans-speak.loca.lt/github-webhook/`.

> [!WARNING]
> There is a difference between `<url>/github-webhook/` and `<url>/github-webhook`. If you use `<url>/github-webhook`, you'll likely run into 302 status codes. See this [post](https://stackoverflow.com/a/51545557) on Stack Overflow.

