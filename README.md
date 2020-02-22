## Command line instructions

### Git global setup
```
git config --global user.name "王顺云"
git config --global user.email "wangsy@changrentech.com"
```

### Create a new repository

```
git clone http://192.168.2.2:6006/wangsy/Launcher.git
cd Test_Launcher
touch README.md
git add README.md
git commit -m "add README"
git push -u origin master
```


### Existing folder

```
cd existing_folder
git init
git remote add origin http://192.168.2.2:6006/wangsy/Launcher.git
git add .
git commit -m "Initial commit"
git push -u origin master
```


### Existing Git repository

```
cd existing_repo
git remote rename origin old-origin
git remote add origin http://192.168.2.2:6006/wangsy/Launcher.git
git push -u origin --all
git push -u origin --tags
```


