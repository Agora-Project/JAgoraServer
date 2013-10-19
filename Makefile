INSTALLATION := $(`pwd`)

# COMPILATION

JFLAGS := -g:none
JC := javac

JAR_FILE := JAgoraServer.jar

JARFLAGS := cf JAgoraServer.jar
JAR := jar

DAEMONLIB := commons-daemon-1.0.15.jar

LIB_DIR := lib

CP := $(LIB_DIR)/$(DAEMONLIB)

SRC_DIR := ./src
BIN_DIR := ./bin

JAVA_FILES := $(shell find $(SRC_DIR) -name '*.java')

MANIFEST_FILE := install/agora.mf

#### INSTALLATION

# Libraries and dependencies
INSTALL_DIR := /usr/lib/agora
INSTALL_FILES = $(JAR_FILE) $(DAEMONLIB)

OWNERS := root:root
PERMISSIONS := u=rwx,g=rx,o=rx

# Init script
AGORA_SCRIPT:=agorad
INIT_DIR := /etc/init.d
INIT_PERMISSIONS := u=rwx,g=rx,o=rx

INIT_SCRIPT := update-rc.d
INIT_SCRIPTOPTS := defaults 10

INIT_SCRIPT_REMOVE := remove

##### RULES

.PHONY: bindir installdir classfiles clean purge

# Building
all: classfiles
	jar cmf $(MANIFEST_FILE) $(LIB_DIR)/$(JAR_FILE) $(BIN_DIR)

classfiles: bindir
	$(JC) $(JFLAGS) -d $(BIN_DIR) -cp $(CP) $(JAVA_FILES)

bindir:
	mkdir -p $(BIN_DIR)


# Installing
install: installdir
	cd $(LIB_DIR) ; \
	cp -r $(INSTALL_FILES) $(INSTALL_DIR)
	cd $(INSTALL_DIR) ; \
	chown $(OWNERS) $(INSTALL_FILES) ; \
	chmod $(PERMISSIONS) $(INSTALL_FILES)
	cp install/$(AGORA_SCRIPT) $(INIT_DIR) ; \
	chown $(OWNERS) $(INIT_DIR)/$(AGORA_SCRIPT) ; \
	chmod $(INIT_PERMISSIONS) $(INIT_DIR)/$(AGORA_SCRIPT) ; \
	$(INIT_SCRIPT) $(AGORA_SCRIPT) $(INIT_SCRIPTOPTS)
	
	
installdir:
	mkdir -p $(INSTALL_DIR)
	
	

clean:
	rm -rf $(BIN_DIR)/*
	rm -f $(LIB_DIR)/$(JAR_FILE)

purge:
	$(INIT_SCRIPT) -f $(AGORA_SCRIPT) $(INIT_SCRIPT_REMOVE)
	rm -f $(INIT_DIR)/$(AGORA_SCRIPT)
	rm -rf $(INSTALL_DIR)
	
	
	
	
	