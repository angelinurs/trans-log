# trans-log

## Contact

- email : angelinurs@hotmail.com

## provisioning-test-environment

- ssh-server, database

    ```zsh
    cd ./docker
    docker compose up -d
    ```

## running

- gradle on console

    ```zsh
    $ ./gradlew bootrun \
            --args='--date=240820 --sysCode=sys01' \
            -Dspring.profiles.active=dev
    ```

## Check

- application log

    ```zsh
    tree ./logs
    ```

- databse

    ```zsh
    $ psql -U naru -h localhost -p 5432 -d backend -W
    backend=# select * from LOG_FILE_STATUS;
    ```

- trans-log

    ```zsh
    $ ssh naru@localhost -p 11122
    [naru@ceb0597dd0a5 ~]$ ls ./storage/
    [naru@ceb0597dd0a5 ~]$ cat ./storage/smp-20240820.log
    ```
  
