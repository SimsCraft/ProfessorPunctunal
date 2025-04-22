package com.simcraft.utility;

/**
 * Represents a bearing (direction) in 2D space, measured in degrees. Bearings
 * are used to indicate direction from one Cartesian point to another and are
 * normalized to the range [0, 360).
 *
 * Bearings are defined as follows: - 0 degrees = North (up) - 90 degrees = East
 * (right) - 180 degrees = South (down) - 270 degrees = West (left)
 *
 * Bearings greater than or equal to 360 degrees will be reduced modulo 360.
 */
public final class Bearing2D {

    // ----- INSTANCE VARIABLES -----
    private double degrees;

    // ----- CONSTRUCTORS -----
    /**
     * Creates a Bearing2D instance with a specified angle in degrees. The angle
     * is automatically normalized to the range [0, 360).
     *
     * @param degrees The initial bearing angle in degrees.
     */
    public Bearing2D(double degrees) {
        this.degrees = normalize(degrees);
    }

    /**
     * Creates a Bearing2D instance representing the angle from one Cartesian
     * point to another.
     *
     * @param startX The x-coordinate of the starting point.
     * @param startY The y-coordinate of the starting point.
     * @param endX The x-coordinate of the ending point.
     * @param endY The y-coordinate of the ending point.
     */
    public Bearing2D(final int startX, final int startY, final int endX, final int endY) {
        this.degrees = normalize(Math.toDegrees(Math.atan2(endY - startY, endX - startX)));
    }

    // ----- GETTERS -----
    /**
     * Returns the bearing angle in degrees, normalized to the range [0, 360).
     *
     * @return The bearing angle.
     */
    public double getDegrees() {
        return degrees;
    }

    // ----- SETTERS -----
    /**
     * Sets a new bearing angle in degrees. The value is automatically
     * normalized to the range [0, 360).
     *
     * @param degrees The new bearing angle in degrees.
     */
    public void setDegrees(double degrees) {
        this.degrees = normalize(degrees);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Normalizes an angle to ensure it falls within the range [0, 360).
     *
     * @param degrees The angle to normalize.
     * @return The normalized angle in degrees.
     */
    private double normalize(final double degrees) {
        return Math.floorMod((int) degrees, 360);
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Compares this Bearing2D object with another object for equality.
     *
     * @param obj The object to compare.
     * @return {@code true} if the object is a {@code Bearing2D} with the same
     * angle, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Bearing2D)) {
            return false;
        }

        Bearing2D b = (Bearing2D) obj;
        return Double.compare(degrees, b.getDegrees()) == 0;
    }

    /**
     * Returns the hash code for this Bearing2D instance.
     *
     * @return A hash code based on the bearing angle.
     */
    @Override
    public int hashCode() {
        return Double.hashCode(degrees);
    }

    /**
     * Returns a string representation of the Bearing2D.
     *
     * @return A string representing the bearing in degrees.
     */
    @Override
    public String toString() {
        return String.format("Bearing2D{degrees=%.2f}", degrees);
    }
}
