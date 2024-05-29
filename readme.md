# Project README

## Prerequisites

Before running the implementations and tests, ensure you have the following installed on your computer:

1. **Java** (version 8 or higher)
2. **SBT** (Scala Build Tool)

## Installation

### Step 1: Install Java

- **Windows:**
  - Download and install Java from the [official website](https://www.oracle.com/java/technologies/javase-downloads.html).
  - Set up the `JAVA_HOME` environment variable.

- **MacOS:**
  - Install Java using Homebrew:
    ```sh
    brew install openjdk@8
    ```
  - Add the following to your `.bash_profile` or `.zshrc`:
    ```sh
    export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
    ```
  - Reload the shell configuration:
    ```sh
    source ~/.bash_profile
    ```

- **Linux:**
  - Install Java using your package manager. For example, on Ubuntu:
    ```sh
    sudo apt-get update
    sudo apt-get install openjdk-8-jdk
    ```
  - Set up the `JAVA_HOME` environment variable:
    ```sh
    export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
    ```

### Step 2: Install SBT

- **Windows and MacOS:**
  - Follow the instructions on the [SBT download page](https://www.scala-sbt.org/download.html).

- **Linux:**
  - For Debian-based systems:
    ```sh
    echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
    sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
    sudo apt-get update
    sudo apt-get install sbt
    ```

## Project Setup

1. **Download the project code:**
   - Clone the repository or download the code package.

2. **Navigate to the project directory:**
   ```sh
   cd /path/to/project/assignment/cp2324/


## Running the Implementations

1. **Run the project using SBT:**
   ```sh
   sbt run

2. **Choose the implementation you want to test from the presented options.**


## Additional features

1. **The pseuCo CCS implementations can be found in the folder:**
    ```sh
    /assignment/pseuco/
