SUMMARY = "Freescale GPU SDK Samples"
DESCRIPTION = "Set of sample applications for Freescale GPU"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=be67a88e9e6c841043b005ad7bcf8309"
DEPENDS = "${X11_DEPENDS} ${WL_DEPENDS} devil assimp gstreamer1.0 gstreamer1.0-plugins-base"
DEPENDS_append_mx6q = " virtual/libgles2 virtual/libg2d"
DEPENDS_append_mx6dl = " virtual/libgles2 virtual/libg2d "
DEPENDS_append_mx6sx = " virtual/libgles2 virtual/libg2d "
DEPENDS_append_mx6sl = " virtual/libopenvg virtual/libg2d"
DEPENDS_append_mx8 = " virtual/libgles2 virtual/libg2d"
DEPENDS_append_mx7ulp = " virtual/libgles2 virtual/libg2d"

X11_DEPENDS = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xrandr', '', d)}"
WL_DEPENDS = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)}"

inherit fsl-eula-unpack

# For backwards compatibility
RPROVIDES_${PN} = "vivante-gpu-sdk"
RREPLACES_${PN} = "vivante-gpu-sdk"
RCONFLICTS_${PN} = "vivante-gpu-sdk"

SRC_URI = "${FSL_MIRROR}/${PN}-${PV}.bin;fsl-eula=true"

SRC_URI[md5sum] = "eee67e79b7fb7054a387083b9b51c11f"
SRC_URI[sha256sum] = "ec8ede790d89976c7ff5154d673bc58d7e5c67d68129a4f67c68f3d6a7c03973"

BACKEND = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'Wayland', \
                bb.utils.contains('DISTRO_FEATURES', 'x11', 'X11', 'FB', d), d)}"

HAS_VPU = "1"
HAS_VPU_mx6sx = "0"

IS_MX8 = "0"
IS_MX8_mx8 = "1"

do_compile () {
    export FSL_GRAPHICS_SDK=${S}
    export FSL_PLATFORM_NAME=Yocto
    export ROOTFS=${STAGING_DIR_HOST}
    cd ${S}/.Config
    ./FslBuild.py -t sdk -- -j 2 EGLBackend=${BACKEND} ROOTFS=${STAGING_DIR_HOST}

}

do_install () {
    export FSL_GRAPHICS_SDK=${S}
    export FSL_PLATFORM_NAME=Yocto
    install -d "${D}/opt/${PN}"
    cd ${S}/.Config
    ./FslBuild.py -t sdk -- -j 2 EGLBackend=${BACKEND} install
    cp -r ${S}/bin/* ${D}/opt/${PN}

    rm -rf ${D}/opt/${PN}/GLES2/DirectMultiSamplingVideoYUV
    rm -rf ${D}/opt/${PN}/GLES3/DirectMultiSamplingVideoYUV
    rm -rf ${D}/opt/${PN}/GLES2/DeBayer

    if [ "${IS_MX8}" = "1" ]; then
        rm -rf ${D}/opt/${PN}/G2D/EightLayers
    fi
}

FILES_${PN} += "/opt/${PN}"
FILES_${PN}-dbg += "/opt/${PN}/*/*/.debug /usr/src/debug"
INSANE_SKIP_${PN} += "already-stripped rpaths"

COMPATIBLE_MACHINE = "(mx6|mx8|mx7ulp)"
