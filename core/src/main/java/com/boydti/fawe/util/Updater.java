package com.boydti.fawe.util;

import com.boydti.fawe.Fawe;
import com.boydti.fawe.FaweVersion;
import com.boydti.fawe.config.Settings;
import com.boydti.fawe.object.FawePlayer;
import com.boydti.fawe.util.chat.Message;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

public class Updater {
    private FaweVersion newVersion;
    private String changes;

    private volatile boolean pending;
    private File pendingFile, destFile;
    private String versionString;

    public synchronized String getChanges() {
        changes = ""; // Unofficial version
        return changes;
    }

    public synchronized boolean isOutdated() {
        return newVersion != null;
    }

    public boolean hasPending(FawePlayer fp) {
        return (pending && fp.hasPermission("fawe.admin"));
    }

    public synchronized void confirmUpdate(FawePlayer fp) {
        // Unofficial version
    }

    public synchronized boolean installUpdate(FawePlayer fp) {
        if (pending && (fp == null || fp.hasPermission("fawe.admin")) && pendingFile.exists()) {
            pending = false;
            File outFileParent = destFile.getParentFile();
            if (!outFileParent.exists()) {
                outFileParent.mkdirs();
            }
            pendingFile.renameTo(destFile);
            return true;
        }
        return false;
    }

    public synchronized void getUpdate(String platform, FaweVersion currentVersion) {
        if (currentVersion == null || platform == null) {
            return;
        }
        try {
            String downloadUrl = "https://ci.athion.net/job/FastAsyncWorldEdit/lastSuccessfulBuild/artifact/target/FastAsyncWorldEdit-%platform%-%version%.jar";
            String versionUrl = "https://empcraft.com/fawe/version.php?%platform%";
            URL url = new URL(versionUrl.replace("%platform%", platform));
            try (Scanner reader = new Scanner(url.openStream())) {
                this.versionString = reader.next();
                FaweVersion version = new FaweVersion(versionString);
                if (version.isNewer(newVersion != null ? newVersion : currentVersion)) {
                    newVersion = version;
                    URL download = new URL(downloadUrl.replaceAll("%platform%", platform).replaceAll("%version%", versionString));
                    try (ReadableByteChannel rbc = Channels.newChannel(download.openStream())) {
                        File jarFile = MainUtil.getJarFile();

                        File finalFile = new File(jarFile.getParent(), "update-confirm" + File.separator + jarFile.getName());
                        File outFile = new File(jarFile.getParent(), "update-confirm" + File.separator + jarFile.getName().replace(".jar", ".part"));
                        boolean exists = outFile.exists();
                        if (exists) {
                            outFile.delete();
                        } else {
                            File outFileParent = outFile.getParentFile();
                            if (!outFileParent.exists()) {
                                outFileParent.mkdirs();
                            }
                        }
                        try (FileOutputStream fos = new FileOutputStream(outFile)) {
                            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                        }
                        outFile.renameTo(finalFile);

                        if (Settings.IMP.UPDATE.equalsIgnoreCase("true")) {
                            pending = true;
                            pendingFile = finalFile;
                            destFile = new File(jarFile.getParent(), "update" + File.separator + jarFile.getName());
                            
                            installUpdate(null);
                            Fawe.debug("Updated FAWE to " + versionString + " @ " + pendingFile);
                            // Unofficial version
                            // MainUtil.sendAdmin("&a/restart&7 to update FAWE with these changes: &c/fawe changelog &7or&c " + "https://empcraft.com/fawe/cl?" + Integer.toHexString(currentVersion.hash));
                        } else if (!Settings.IMP.UPDATE.equalsIgnoreCase("false")) {
                            pendingFile = finalFile;
                            destFile = new File(jarFile.getParent(), "update" + File.separator + jarFile.getName());
                            pending = true;

                            for (final FawePlayer<?> player : Fawe.get().getCachedPlayers()) {
                                confirmUpdate(player);
                            }
                        }
                    }
                }
            }
        } catch (Throwable ignore) {
        }
    }
}
