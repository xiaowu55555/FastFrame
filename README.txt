# FastFrame
# 快速开发框架

how to use

添加 compileOptions {

    sourceCompatibility JavaVersion.VERSION_1_8

    targetCompatibility JavaVersion.VERSION_1_8

}

-Step 1 
Add it in your root build.gradle at the end of repositories:
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
-Step 2
Add the dependency:
	dependencies {
	        implementation 'com.github.xiaowu55555:FastFrame:1.0'
	}
