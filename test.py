import subprocess
import os
from subprocess import check_output
import re 

def clean():
    cmd = 'rm -r ./b ./y ./x ./c'
    proc = subprocess.Popen(cmd, shell=True)
    proc.communicate()

def make_folders():
    cmd = 'mkdir ' + './b'
    proc = subprocess.Popen(cmd, shell=True)
    cmd = 'mkdir ' + './y'
    proc = subprocess.Popen(cmd, shell=True)
    cmd = 'mkdir ' + './x'
    proc = subprocess.Popen(cmd, shell=True)
    cmd = 'mkdir ' + './c'
    proc = subprocess.Popen(cmd, shell=True)
    proc.communicate()
                
def comp(directory):
    cmd = 'mvn -f ' + directory + ' compile' 
    proc = subprocess.Popen(cmd, shell=True)
    proc.communicate()
    
def install(directory):
    cmd = 'mvn -f ' + directory + ' install' 
    proc = subprocess.Popen(cmd, shell=True)
    proc.communicate()
    
def jcomp(directory):
    for folder in os.listdir(directory):
    	for filename in os.listdir(directory + folder):
            cmd = 'javac -cp ' + directory + folder + '/ ' + directory + folder + '/' + filename 
            proc = subprocess.Popen(cmd, shell=True)
            proc.communicate()
    
def move(fr,to):
    for filename in os.listdir(fr):
        if filename.endswith(".class"): 
            filename = filename.replace('$','\$')
            cmd = 'mv ' + fr + filename + " " + to
            proc = subprocess.Popen(cmd, shell=True)
            proc.communicate()
        else:
            continue
            
def movesuper(fr,to):
    for filename in os.listdir(fr):
        if filename == "A11.java": 
            for folder in os.listdir(to):
                if folder.startswith("A12-"): 
                    cmd = 'cp ' + fr + filename + " " + to + folder
                    proc = subprocess.Popen(cmd, shell=True)
                    proc.communicate()
        else:
            continue
            
def move2(fr,to):
    for folder in os.listdir(fr):
    
        # extra loop for inner classes
        for filename in os.listdir(fr + folder):
        
            # special case for test A13 which contains A12 super class
            if filename.endswith('.class') and (not (folder.startswith("A12-") and filename.startswith("A11"))): 
                filename = filename.replace('$','\$')
                suffix = '-' + folder.split("-",1)[1] 
                prefix = filename.replace(".class","")
                target = prefix+suffix   	    
                cmd = 'mkdir ' + to + target + '/'
                proc = subprocess.Popen(cmd, shell=True)
                proc.communicate()
                cmd = 'mv ' + fr + folder + '/' + filename + ' ' + to + target + '/'
                proc = subprocess.Popen(cmd, shell=True)
                proc.communicate()
            else:
                continue

def run(run_dir, arg_dir, out_dir, first_program,last_program):
    args = os.getcwd() + arg_dir + " " + os.getcwd() + out_dir + " " + first_program + " " + last_program
    cmd = 'mvn -f ' + run_dir\
    + ' exec:java -Dexec.mainClass="Main" -Dexec.args="' + args + '"'
    proc = subprocess.Popen(cmd, shell=True)
    proc.communicate()
            
def diff():
    results = {}
    for dirname in os.listdir('./c/'):
        classname = re.search(r".*-", dirname).group(0)[:-1]
        output = check_output(["java", '-jar', 'sootdiff.jar', 
        '-otherfile', './c/' + dirname + '/', '-reffile',
        './y/' + dirname + '/', '-qname', classname])
        if not output:
            results[dirname] = True
        else:
            results[dirname] = False
    for key, value in results.items():
        print(key, value)

first_program = "1"
last_program = "12"
clean()
make_folders()
comp("./a/")
install("./spoon/")
move("./a/target/classes/","./x/")
run("./spoon/","/a/src/main/java/","/b/",first_program,last_program)
movesuper("./a/src/main/java/","./b/")
jcomp("./b/")
move2("./b/","./c/")
comp("./transformer/")
run("./transformer/","","",first_program,last_program)
comp("./asm/")
run("./asm/","/x/","/y/",first_program,last_program)
diff()
