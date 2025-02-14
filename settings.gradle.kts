rootProject.name = "my-spring"

fun defineSubProject(name: String, path: String) {
    include(name)
    project(":$name").projectDir = file(path)
}