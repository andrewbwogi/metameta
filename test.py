import subprocess
import os
from subprocess import check_output
import re 

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
    
def jcomp(directory):
    for folder in os.listdir(directory):
    	for filename in os.listdir(directory + folder):
            cmd = 'javac ' + directory + folder + '/' + filename 
            proc = subprocess.Popen(cmd, shell=True)
            proc.communicate()
    
def move(fr,to):
    for filename in os.listdir(fr):
        if filename.endswith(".class"): 
            cmd = 'mv ' + fr + filename + " " + to
            proc = subprocess.Popen(cmd, shell=True)
            proc.communicate()
        else:
            continue
            
def move2(fr,to):
    for folder in os.listdir(fr):
        for filename in os.listdir(fr + folder):
            if filename.endswith('.class'):    	    
                cmd = 'mkdir ' + to + folder + '/'
                proc = subprocess.Popen(cmd, shell=True)
                proc.communicate()
                cmd = 'mv ' + fr + folder + '/' + filename + ' ' + to + folder + '/'
                proc = subprocess.Popen(cmd, shell=True)
                proc.communicate()
            else:
                continue

def run(run_dir, arg_dir, out_dir):
    args = os.getcwd() + arg_dir + " " + os.getcwd() + out_dir
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

make_folders()
comp("./a/")
comp("./spoon/")
move("./a/target/classes/","./x/")
run("./spoon/","/a/src/main/java/","/b/")
jcomp("./b/")
move2("./b/","./c/")
comp("./transformer/")
run("./transformer/","","")
comp("./asm/")
run("./asm/","/x/","/y/")
diff()
