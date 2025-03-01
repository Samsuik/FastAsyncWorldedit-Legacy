package com.boydti.fawe;

public class FaweVersion {
    // public final int year, month, day, hash, build, major, minor, patch;
    public String fawe = "", edition = "", we = "";

    public FaweVersion(String version) {
        String[] split = version.substring(version.indexOf('=') + 1).split("-");
        if (split[0].equals("unknown")) {
            // this.year = month = day = hash = build = major = minor = patch = 0;
            return;
        }
        fawe = split[0];
        edition = split[1];
        we = split[2];
    }

    @Override
    public String toString() {
        return "FastAsyncWorldEditLegacy-" + fawe + "-" + edition + "-" + we;
    }

    public boolean isNewer(FaweVersion other) {
        return other.fawe.compareTo(this.fawe) > 0 && other.we.compareTo(this.we) > 0;
    }
}