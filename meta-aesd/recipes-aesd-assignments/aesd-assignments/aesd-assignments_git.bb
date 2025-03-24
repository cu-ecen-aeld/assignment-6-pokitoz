#inherit systemd
#SYSTEMD_AUTO_ENABLE = "enable"
#SYSTEMD_SERVICE:${PN} = "aesdsocket.service"

# See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

SRC_URI = "git://github.com/cu-ecen-aeld/assignments-3-and-later-pokitoz.git;protocol=https;branch=main"

PV = "1.0+git${SRCPV}"
SRCREV = "7a3b564a839a7729d899e26994ce8a3ce0792f25"

# This sets your staging directory based on WORKDIR, where WORKDIR is defined at
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
# We reference the "server" directory here to build from the "server" directory
# in your assignments repo
S = "${WORKDIR}/git/server"

# Add the aesdsocket application and any other files you need to install
# See https://git.yoctoproject.org/poky/plain/meta/conf/bitbake.conf?h=kirkstone
#FILES:${PN} += "aesdsocket"

# customize these as necessary for any libraries you need for your application
# (and remove comment)
TARGET_LDFLAGS += "-pthread -lrt"
TARGET_CC_ARCH += "${LDFLAGS}"
DEPENDS:append = " update-rc.d-native"
PACKAGE_WRITE_DEPS:append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"
RDEPENDS:${PN} = "initd-functions \
                  ${@bb.utils.contains('DISTRO_FEATURES','selinux','${PN}-sushell','',d)} \
                  init-system-helpers-service \
		 "

do_configure () {
	:
}

do_compile () {
	oe_runmake
}

do_install () {
	# Install your binaries/scripts here.
	# Be sure to install the target directory with install -d first
	# Yocto variables ${D} and ${S} are useful here, which you can read about at
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-D
	# and
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-S
	# See example at https://github.com/cu-ecen-aeld/ecen5013-yocto/blob/ecen5013-hello-world/meta-ecen5013/recipes-ecen5013/ecen5013-hello-world/ecen5013-hello-world_git.bb

	install -d ${D}/usr/bin
	install -m 0755 ${S}/aesdsocket ${D}/usr/bin/aesdsocket

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/aesdsocket-start-stop ${D}${sysconfdir}/init.d/aesdsocket-start-stop

	#install -d ${D}/${sysconfdir}/rc.S
	#install -m 0644 ${S}/aesdsocket-start-stop ${D}${sysconfdir}/rc.S/99.aesdsocket-start-stop.sh

	#install -d ${D}/${systemd_unitdir}/system
	#install -m 0644 ${S}/aesdsocket.service ${D}/${systemd_unitdir}/system
}

inherit update-rc.d

INITSCRIPT_NAME = "aesdsocket-start-stop"
INITSCRIPT_PARAMS = "defaults 99"
FILES:${PN} += "${sysconfdir}/init.d/aesdsocket-start-stop"
