
# Setup

Follow these instructions to set up IntelliJ IDEA Ultimate and your environment.

## IntelliJ

First, [create a Jet Brains academic account](https://www.jetbrains.com/shop/eform/students).

### Download and Install IntelliJ
You can download IntelliJ IDEA Ultimate from [https://jetbrains.com/idea](https://jetbrains.com/idea) and install it.

Video instructions:

* [Windows download and installation](https://northeastern.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=b58332f0-d512-40a5-9097-b0ed001eaf95&start=0)
* [Mac download](https://northeastern.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=a2dde07b-bf3e-43af-b64f-b0e4010de83a&start=0)
and [installation](https://northeastern.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=05f9e00f-ffc2-4233-b18c-b0ed00362ac8&start=0)

### Add License

[Video instructions](https://northeastern.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=e2656560-4711-4fc5-8f43-b0ed0051931f&start=0)

If you get a message that there are no available licenses, open your browser to 
[https://account.jetbrains.com/licenses](https://account.jetbrains.com/licenses), where you should see:

![image](https://github.com/jacquard-autograder/jacquard/assets/661056/a44d96a2-dfb1-417e-85d2-6b4bac2c0278)

Click on the link labeled [Apply for a free student or teacher 
license](https://www.jetbrains.com/student), and follow the instructions there. 

### Install JDK 21

[Video instructions](https://northeastern.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=edc1d4ac-0f28-4e44-b14a-b0ed005186c1&start=0)

## Environment

### Installing git

#### Windows

While not strictly required if you run `git` through IntelliJ, we recommend:

* installing [Git for Windows](https://gitforwindows.org/) ([video instructions](https://northeastern.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=6fae1c27-6b37-4b7c-a3b6-b0e201546e2e&start=0))

* pinning Git Bash to the Taskbar and configuring it to open in your preferred directory
([video instructions](https://northeastern.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=473e830b-bd18-4e87-a1ab-b0ea017447b5&start=0))

#### Mac

Try running `git` from the command line. If it hasn't been installed, you may be prompted to install the
command line developer tools, which you should do. If you get an `xcrun` error, install them manually with
`xcode-select --install` or (if that doesn't work) `sudo xcode-select --reset`.

### Configuring git

If you have not configured git, we recommend running these commands, substituting your information (retaining the quotation marks).
```
git config --global user.name "YOUR NAME"
git config --global user.email "YOUR EMAIL"
```
