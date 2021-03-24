# Chrome
Android 系统 Chrome浏览器插件

1.在项目根 build.gradle 文件中添加 Maven 仓库：
```
allprojects {
    repositories {
        maven {
            url "http://maven.ituns.org/repository/maven-public/"
        }
    }
}
```

2.在模块 build.gradle 中添加引用：
```
dependencies {
    implementation "org.ituns.google:chrome:1.0.1"
}
```