plugins {
    id("ru.mipt.npm.gradle.mpp")
}

description = "Binding for https://github.com/JetBrains-Research/viktor"

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-ast"))
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}
