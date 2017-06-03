

Description-



I have used Java to create a program whose functionality is similar to the host discovery functionality of the Morris Worm. 

Discovery.java is a program that looks for potential hosts in the following ssh related files-

1. /etc/hosts

2. ~/.ssh/config (for each user)

3. /etc/ssh/ssh_config (system wide ssh config file)

4. ~/.ssh/known_hosts (for each user)

5. /etc/ssh/ssh_known_hosts (system wide ssh known hosts file)

6. ~/.ssh/authorized_keys (for each user)



Discover.java will find all potential hostnames in the above files and display them one per line using standard output.

I am reading the user specific files one by one, for every user on the system, and then doing string manipulations to find the potential hostnames.

The system wide configuration files are just read once since they are common for all users, and using similar string manipulations, hostnames are identified.

The program does not alter file permissions, in the sense that, if it is not able to open a certain file due to incomplete permissions, it skips that file and moves on to the next one.