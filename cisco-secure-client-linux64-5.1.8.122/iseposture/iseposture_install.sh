#!/bin/sh
#

BASH_BASE_SIZE=0x0000204f
CISCO_AC_TIMESTAMP=0x0000000067c5b2cc
CISCO_AC_OBJNAME=iseposture_install.sh                                                                                                                                                                                                                                           
# BASH_BASE_SIZE=0x00000000 is required for signing
# the comment is after or else the code signing tool will find the comment

csc_vercmp() 
{
    ver_from="${1:?missing from-version}"
    ver_to="${2:?missing to-version}"

    if [ "$ver_from" = "$ver_to" ]; then
        echo same
    else
        ver_min="$(printf '%s\n' "$ver_from" "$ver_to" | sort -V | head -n1)"
        if [ "$ver_min" = "$ver_from" ]; then
            echo older
        else
            echo newer
        fi
    fi
}

AC_INSTPREFIX="/opt/cisco/anyconnect"
AC_ISEPOSTURE_PROFILEDIR="${AC_INSTPREFIX}/iseposture"
AC_ISEPOSTURE_SCRIPTDIR="${AC_ISEPOSTURE_PROFILEDIR}/scripts"

CSC_INSTPREFIX="/opt/cisco/secureclient"
CSC_BINDIR="${CSC_INSTPREFIX}/bin"
CSC_LIBDIR="${CSC_INSTPREFIX}/lib"
CSC_PLUGINDIR="${CSC_BINDIR}/plugins"
CSC_ISEPOSTURE_PROFILEDIR="${CSC_INSTPREFIX}/iseposture"
CSC_ISEPOSTURE_SCRIPTDIR="${CSC_ISEPOSTURE_PROFILEDIR}/scripts"

ISEBINFILES="csc_iseposture csc_iseagentd iseposture_uninstall.sh manifesttool_iseposture"
ISELIBFILES="libacise.so"
ISEPLUGINFILES="libacisectrl.so libaciseshim.so"
ISEPOSTUREMANIFEST="ACManifestISEPosture.xml"
ISEMIGRATIONFILES="ISEPostureCFG.xml ConnectionData.xml DGCacheRecords.xml ISEPreferences.xml"
VPNMANIFEST="${CSC_INSTPREFIX}/ACManifestVPN.xml"

LOGFNAME=`date "+csc-linux64-5.1.8.122-iseposture-%H%M%S%d%m%Y.log"`
CLIENTNAME="Cisco Secure Client - ISE Posture"
CURRENTDIR=`dirname $0 2> /dev/null`

INST_BINDIR="${CURRENTDIR}/bin"
INST_PLUGINDIR="${INST_BINDIR}/plugins"
INST_LIBDIR="${CURRENTDIR}/lib"

ARG_NO_LICENSE=0

if [ "x$1" = "x--no-license" ]; then
    ARG_NO_LICENSE=1
fi

echo "Installing ${CLIENTNAME}..."
echo "Installing ${CLIENTNAME}..." > /tmp/${LOGFNAME}
echo `whoami` "invoked $0 from " `pwd` " at " `date` >> /tmp/${LOGFNAME}

# Check for root privileges
if [ `id | sed -e 's/(.*//'` != "uid=0" ]; then
  echo "Sorry, you need super user privileges to run this script."
  echo "Sorry, you need super user privileges to run this script." >> /tmp/${LOGFNAME}
  exit 1
fi

# ISE Posture requires VPN to be installed. We check the presence of the vpn manifest file to check if it is installed.
if [ ! -f ${VPNMANIFEST} ]; then
    echo "AnyConnect VPN should be installed before ISE Posture installation. Install Cisco Secure Client - AnyConnect VPN to proceed."
    echo "Exiting now."
    echo "AnyConnect VPN should be installed before ISE Posture installation. Install Cisco Secure Client - AnyConnect VPN to proceed." >> /tmp/${LOGFNAME}
    echo "Exiting now." >> /tmp/${LOGFNAME}
    exit 1
fi

failed=false
# version of ise posture being installed has to be same as installed VPN version
if [ -f "${CURRENTDIR}/${ISEPOSTUREMANIFEST}" ] && [ -f ${VPNMANIFEST} ]; then
    VPNVERSION=$(awk -F"\"" '/file version/ { print $2 }' ${VPNMANIFEST})
    ISECURRVERSION=$(awk -F"\"" '/file version/ { print $2 }' ${CURRENTDIR}/${ISEPOSTUREMANIFEST})

    vpn_ver="$(csc_vercmp "$VPNVERSION" "$ISECURRVERSION")"
    if [ "$vpn_ver" != same ]; then
     failed=true
    fi
fi

if [ "$failed" = true ]; then
    echo "Please use ise posture installer from Cisco Secure Client package with version ${VPNVERSION} for the installation"
    echo "Please use ise posture installer from Cisco Secure Client package with version ${VPNVERSION} for the installation" >> /tmp/${LOGFNAME}
    echo "Exiting now."
    echo "Exiting now." >> /tmp/${LOGFNAME}
    exit 1
fi

if [ "x${ARG_NO_LICENSE}" = "x1" ]; then
    echo "Skipping license text ..."
else
    if [ -f "license.txt" ]; then
        cat ./license.txt
        echo
        echo -n "Do you accept the terms in the license agreement? [y/n] "
        read LICENSEAGREEMENT
        while :
        do
          case ${LICENSEAGREEMENT} in
               [Yy][Ee][Ss] | [Yy])
                       echo "You have accepted the license agreement."
                       echo "Please wait while ${CLIENTNAME} is being installed..."
                       break
                       ;;
               [Nn][Oo] | [Nn])
                       echo "The installation was cancelled because you did not accept the license agreement."
                       echo "The installation was cancelled because you did not accept the license agreement." >> /tmp/${LOGFNAME}
                       exit 1
                       ;;
               *)
                       echo "Please enter either \"y\" or \"n\"."
                       read LICENSEAGREEMENT
                       ;;
          esac
        done
    else
        echo "License file not found. Aborting installation."
        echo "License file not found. Aborting installation." >> /tmp/${LOGFNAME}
        exit 1
    fi
fi
if [ -x "/usr/bin/install" ]; then
    INSTALL="/usr/bin/install"
elif [ -x "/bin/install" ]; then
    INSTALL="/bin/install"
elif [ -x "/usr/local/bin/install" ]; then
    INSTALL="/usr/local/bin/install"
else
    INSTALL="install"
fi

${INSTALL} --help 2> /dev/null > /dev/null
if [ $? != 0 ]; then
    INSTALL=""
fi

echo "Creating directories... "
echo "Creating directories... " >> /tmp/${LOGFNAME}

if [ "x${INSTALL}" = "x" ]; then
    echo "Unable to find install command. Aborting installation."
    echo "Unable to find install command. Aborting installation." >> /tmp/${LOGFNAME}
    exit 1
fi

# Make sure destination directories exist
echo "Installing "${CSC_BINDIR} >> /tmp/${LOGFNAME}
${INSTALL} -d ${CSC_BINDIR} || exit 1
echo "Installing "${CSC_LIBDIR} >> /tmp/${LOGFNAME}
${INSTALL} -d ${CSC_LIBDIR} || exit 1
echo "Installing "${CSC_PLUGINDIR} >> /tmp/${LOGFNAME}
${INSTALL} -d ${CSC_PLUGINDIR} || exit 1
echo "Installing "${CSC_ISEPOSTURE_PROFILEDIR} >> /tmp/${LOGFNAME}
${INSTALL} -d ${CSC_ISEPOSTURE_PROFILEDIR} || exit 1
echo "Installing "${CSC_ISEPOSTURE_SCRIPTDIR} >> /tmp/${LOGFNAME}
${INSTALL} -d ${CSC_ISEPOSTURE_SCRIPTDIR} || exit 1

echo "done."
echo "done." >> /tmp/${LOGFNAME}

# Migrate /opt/cisco/anyconnect/iseposture/ files to /opt/cisco/secureclient/iseposture/
for file in ${ISEMIGRATIONFILES}; do
    if [ -f ${AC_ISEPOSTURE_PROFILEDIR}/$file ] &&
       [ ! -f ${CSC_ISEPOSTURE_PROFILEDIR}/$file ]; then
      echo "Migrating "${AC_ISEPOSTURE_PROFILEDIR}/$file >> /tmp/${LOGFNAME}
      cp -f ${AC_ISEPOSTURE_PROFILEDIR}/$file ${CSC_ISEPOSTURE_PROFILEDIR}/ >/dev/null 2>&1
    fi
done
if [ -d "${AC_ISEPOSTURE_SCRIPTDIR}" ]; then
    echo "Migrating "${AC_ISEPOSTURE_SCRIPTDIR}/ >> /tmp/${LOGFNAME}
    tar cf - -C ${AC_ISEPOSTURE_SCRIPTDIR} . | (cd ${CSC_ISEPOSTURE_SCRIPTDIR}; tar --skip-old-files -x -f -)
fi

echo "Copying files... "
echo "Copying files... " >> /tmp/${LOGFNAME}

for f in ${ISEBINFILES}; do
    echo "Installing "${INST_BINDIR}/$f >> /tmp/${LOGFNAME}
    ${INSTALL} -o root -m 755 ${INST_BINDIR}/$f ${CSC_BINDIR} || exit 1
done

for f in ${ISELIBFILES}; do
    echo "Installing "${INST_LIBDIR}/$f >> /tmp/${LOGFNAME}
    ${INSTALL} -o root -m 755 ${INST_LIBDIR}/$f ${CSC_LIBDIR} || exit 1
done

for f in ${ISEPLUGINFILES}; do
    echo "Installing "${INST_PLUGINDIR}/$f >> /tmp/${LOGFNAME}
    ${INSTALL} -o root -m 755 ${INST_PLUGINDIR}/$f ${CSC_PLUGINDIR} || exit 1
done

echo "done."
echo "done." >> /tmp/${LOGFNAME}

# update manifest
echo "Updating AC manifest." >> /tmp/${LOGFNAME}

${INSTALL} -o root -m 755 ${CURRENTDIR}/${ISEPOSTUREMANIFEST} ${CSC_INSTPREFIX} >> /tmp/${LOGFNAME}
${INST_BINDIR}/manifesttool_iseposture -i ${CSC_INSTPREFIX} ${CSC_INSTPREFIX}/${ISEPOSTUREMANIFEST} >> /tmp/${LOGFNAME}

# enable GUI launch at login
if [ -f "${CSC_BINDIR}/acinstallhelper" ]; then
    echo "Enabling Cisco Secure Client - GUI launch at login." >> /tmp/${LOGFNAME}
    ${CSC_BINDIR}/acinstallhelper -launchAtLogin -enable
fi

echo "done."
echo "done." >> /tmp/${LOGFNAME}

echo "${CLIENTNAME} is installed successfully."
echo "${CLIENTNAME} is installed successfully." >> /tmp/${LOGFNAME}

# move the logfile out of the tmp directory
mv /tmp/${LOGFNAME} ${CSC_INSTPREFIX}/.

exit 0


  +S!cV!j!dqeayIVDMPT!A!kfkjvddSBJJJ!V!eznRGBF r0‚n0‚V G¤4ö	‚c÷ß`Ji0	*†H†÷ 0i10	UUS10U
DigiCert, Inc.1A0?U8DigiCert Trusted G4 Code Signing RSA4096 SHA384 2021 CA10240329000000Z270404235959Z0v10	UUS10UMassachusetts10U
Boxborough10U
Cisco Systems, Inc.10UCisco Systems, Inc.0‚"0	*†H†÷ ‚ 0‚
‚ ±à»¼OÓ‹“‡¯;˜áh;O#¶~&ñxÈRZácœ‹§H%ržÚÀ%7âù¶žô‘/š?xž­œ¤TËÈ¼*~SN\%¦þÄ¼s\bÚŸ˜ "|=LÄnŒ›cÛ<¶ÿÉ•Ò£Ô×òƒÜlß¦î¤Íœ›•H¾?ÿÌ0SÜX•¸K’XZ½œéÀûLÞ1‘O‰mŽÿƒwi¹sâ˜pÅ.Ö,²ÞHè®?U+Õ+úr`ÊT#'9_ÑŸ1>15#=žbKÂèÎw€ºº.:Å›ƒ@ÉI/ïÊÐn¼‹Üu°5-£}QÈù*Š¸[H¾ŸNAéÙÏàßIŒÊi‘÷Ãþ<·ÝuØ'€öúÏû†ÏØË‡?f)._`lñÒo÷ì_¨fèêâ
¢µ;M9æ¾¿Ü¡<]Ò¡ß|¨Í²LX 6öøÿÇ6ù+‘Ö“â?,XÀiš1®žD¥Â'tkßåèEF=!ÖËcì© ¨PKí‚Ûw4õõ3Hv•o[]ßÒIÌµÔ ¹½˜Lo9ŽëÀÇÕåÐµTæœè_j–Ôt ”‹$#&GƒdQ
ˆ]‘FD¶ùåÐÝròU´TbÐÆºˆ”-4A“Žø´ìf`˜©v™YA®F›ˆ‰¾ƒÛÿ.Ò>`³¹W…ÅS¶Ïi”ç™Þ”Ý £‚0‚ÿ0U#0€h7àë¶;ø_†ûþa{ˆeôNB0UÒtÀb=_r•3þ;køù£*0¿0>U 70503g0)0'+http://www.digicert.com/CPS0Uÿ€0U%0
+0µU­0ª0S Q O†Mhttp://crl3.digicert.com/DigiCertTrustedG4CodeSigningRSA4096SHA3842021CA1.crl0S Q O†Mhttp://crl4.digicert.com/DigiCertTrustedG4CodeSigningRSA4096SHA3842021CA1.crl0”+‡0„0$+0†http://ocsp.digicert.com0\+0†Phttp://cacerts.digicert.com/DigiCertTrustedG4CodeSigningRSA4096SHA3842021CA1.crt0	U0 0	*†H†÷ ‚ À‘¢q&~	]æ¢¦Fäþ7VçÎ7¥ú¸ËŒÀ­y¹­±NËñ£jåÿzÑô’¤GGE@ùßGžÒf2_Qc´Ã"wU@À+òš‘™ÈÎQß‚&ök>QØ!‚Ýà·‘â€L8€°Úú)zŒ0¬‹ÍÒ˜voèþçrÂ¾ÊiFBa¨Š˜…9fèÇ0\Mñq­oE>GgÞ½˜«Ôýc·OZ”ùå’''§lo¿îâÁ«õõèÌ
<í=»
þŠÁ½PŒb$“1ƒëgn­mGüÂ?y¼X PFü Â.öòÒ€PÖ;0Ì·Ó+¨„UÕë8Q
u ßósÃÀr¦ÑÐ}œx#bìœAõªÔÖ³È_ŽA “¯ù·íÂÐšiüÍÊ§ÎO8ÃF=ü]Î˜øu¯ 3aÔr1þäå!Çî¦ý°‚6”g»“‰L^ìiD‹lXâM‹Þ.FÀÿ¤ÜÍ|€tgÙ-’x¦aÎ4áZZ8(Ê4ÔJoiGÒM…vÜí>6Þ‚Ç4)Øœ ¼µ\‹†ƒHãîy£·Ç&Wq_e§·¥;å®Áù¯D¾*2ÃÑ›Ô.`ÚÌÉàmêšüÕµë¶ó—€Ý0žSúÇ`ÓL“<0,òÑ¦]‹¸Y¶×å1»DA6ýëç ´0‚°0‚˜ ­@²`ÒœLŸ^Í©½“®Ù0	*†H†÷ 0b10	UUS10U
DigiCert Inc10Uwww.digicert.com1!0UDigiCert Trusted Root G40210429000000Z360428235959Z0i10	UUS10U
DigiCert, Inc.1A0?U8DigiCert Trusted G4 Code Signing RSA4096 SHA384 2021 CA10‚"0	*†H†÷ ‚ 0‚
‚ Õ´/BÐ(­x·]Õ9Y±ˆBõ3Œë=—pÅ»Ä…&0Ÿ¤ŽhØ\õë4$áKOÓxCô×ÚùÒÕgRO¡Wüˆ™Á‘Ì>Mp$d³â}4}L€WkCš™òÅ;òïËGZ¦L³ %óÓ‹²ûðŠà	Àe§ú˜€I5‡=Qè!xE.¡Ÿ,áÂÌÅî“IpB2ûÆêóeh‘¢ML‚HRhÞ½W]èeÅ²O…,H¤V„5Öù.œª Ñ7þ”Â|Èê2æÊÂô§£t¥¯9¶«`ãèÖ¹÷1áÜ­ä ØÁ#GG³¡n£«m˜ƒ·jg-Ò½D’°;	×Ç›2ZÂÿj`T‹IÁ“íá´\àoë&ùŒÕ²ù8æêÎ‘õ¾Óûo“a4\¼“E(ƒ6*f(_°sÎ‹&%²ƒÔ\öLíbà^3òèèì
§°+‘²6y¾÷­u¦eÌ»ãHPów‘þÛP¢FÈaX˜õ|<ƒ(­9†ìÔ·SÐøGæu0ì0“v¦[K]taM?‘vÞ¿XËr)AðÕÅm&vhAXšÜ&+ô‰MYÛxÏJ>@G_ÉPs…#!Y`ŠdTÁÌ!è8|fÍx8E0™OÿcOL»ªSA|X=G³ú¶ìŒ2	Ìl<V £‚Y0‚U0Uÿ0ÿ 0Uh7àë¶;ø_†ûþa{ˆeôNB0U#0€ì×ã‚Òq]dLß.g?çº˜®O0Uÿ†0U%0
+0w+k0i0$+0†http://ocsp.digicert.com0A+0†5http://cacerts.digicert.com/DigiCertTrustedRootG4.crt0CU<0:08 6 4†2http://crl3.digicert.com/DigiCertTrustedRootG4.crl0U 00g0g0	*†H†÷ ‚ :#D=vî¼:™ÓVà¥øH4ó,¶ætf÷”r± Ê¯l0'žDŸKýž£|&Õ7¼:]Hm•Õ?Iô'»EPýœ½¶…àv~7qË"÷ZªÏõ“jãë ÑÕPyˆšŠŠÁ¶½¡H~ÜØ€Ía™VöÉãvçÄä_Cø>”ÿv9=žÔ™ÏJÝ(ë_&¡•XHÕþ×'?ýÑv†Ý°`\ó¨îà‰¡½9á8NÚn»6ûå!SZÃÊéjñ¢>ÛC¸3ÈO8’™õÝÎTmÙ]@3|â•²Â!usRËFØÄ4¢¥KÍov7,…?Î&é¾°C•ˆ ‚pðÌÊïý)5\‰8U÷7Š‹	¡Ëé1ÿ.\9qá¾œ§
Ö&g·’æN_Þz¬IÏ.¤t’­Û<¤œ†ãÁV+#ÿµêˆ{pkæ ºý:?E¦Äè‘R‹AÀH„K–M«D@ãð(ÎíñV*/Äd<3®(Œ<Ëˆ¿;ôÎ{ŽïµëË·ðwæç(?¬®¥/"lAù‚\fÌlÊÅ†Ãö&3K Ójj@0HK4¨Q »­…âYmÊP+êJž¥ý §tçòÖRý¯…H!ùW;´œí†Eô´an¿hâ&`†êÈ¯©þ”çc:†VxN  V"øjOä‰øÔjÐK¦ã¬-ÃŸ¼n&¾õc`èsoîØ%óW$ß7?.mìÜxcÎW//Ì"~Å}´’—ô&Qh”ÝšNÏà]‘mc1;Ê »°îqKÅÑ3”UM¾c`î Ÿœ;µˆx÷pˆwûÖÖ›ô;Øðé‹!¬k¯º¯r¿;¶Aì‹´Ÿ}v@KVîÝ%Ž?ý¨W¢8FLù· 7Å’ò¿8VI±>bËÃdD÷Û“ kK¸‹«FÄÜo|è×x7“Üª…2n<°cžŽ+ Åú]‘Ÿl’Q€®P®õ$P`¯Kø‰æ[²Fœœöà]y×E_Ú÷bpÃÙÊrÉÂ|š«Ç¿–¦rB$àSªM‡54üm–Éå¶¨ý˜Ì>^	-Å\óæ43
;`á9(F/¥6«‘”'—"/ìå!Cq?©ƒ¿qÏqh‚‰zv¢ ˆ–ÁÛÞêkm°æ#µ¦ox‡ìÉyYì®‚)‰£.%§RïsdÍ®c,;ÎÚ~å¿}Ìa<€­ìkyüAÚj¬[7²X^¨S~f¤%Æ/îqþ0GT:>Ñêýa!èO:ˆ¯DR>&ÙäòÉ:¢Ä3„¼ñmö§¤Ðb`–6ø¥{ÎÎØ+lQÂ|ãôÌ;‘j»  ˆ¤Ùf>Ó]ÓÿÛBpc†eÈß*(ÝÅò=^H‰{Ò1ä'73(ªÕ¾ÐnÏ9d×¹Ž<¸U¬5¡Eê,¡SRVtEÑ‰´X¹+ÿQc¬´­bú­«Ccµ»J•€y™ö$žâ›¼”ÚH7im C=–Šl.@·pµùÑ‰Íìb<½k‡l³•*^ÎM6‡ºÉeökÜ¹ëéßÂ¤wp¡g[Ó£«ÔôìNjÏÇEÐïTýê(É¥©HoÜ±rÄ?o–†g+:ÂÚ8Ú3T£|§"iIƒdµ{¡zkt"Ó¼`yÈs´Üóª^ðÄèä 2¹èš,”ÿäý<†¼MÂäà=ÿ2eï™«‹;aÑÒZ9Ÿÿ—>ð u?†­öâ?z‰Ì¾,®$¹?±ß˜´ûÃyl}\8ç%h¥^™ËPuiiÑ¿­J°t‘.‡T¶ï¿HAå–T%¡ÂÂ«VÏOð`»¬Cê ZÁ…°Ý¨<.­¼}Êü€$¬Ø'Rú÷w¯;Ý³ñWp/2bÿ‰1í¸ß1®Êõ‰ÂqE¹Ì±Ÿ÷ûsPúD i	Ý0µs—¡ÒJxÕL‰IÄB55ÍéÙ€¨ô³ÑÑËnuín¬ÃÞBÙú;*d^d¤þéÃý­ˆ1ÇÔzÂÀ_