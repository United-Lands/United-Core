package org.unitedlands.items.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class GenericLocation implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final int x;
	private final int y;
	private final int z;
	private final String worldName;

	public GenericLocation(int x, int y, int z, String worldName) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldName = worldName;
	}

	public GenericLocation(Location location) {
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.worldName = location.getWorld().getName();
	}

	public Location getLocation() {
		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			Bukkit.getLogger().warning("[UnitedItems] World '" + worldName + "' is not loaded! Cannot restore sapling at: " + this);
			return null;
		}
		return new Location(world, x, y, z);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GenericLocation that = (GenericLocation) o;
		return x == that.x && y == that.y && z == that.z && worldName.equals(that.worldName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z, worldName);
	}

	@Override
	public String toString() {
		return String.format("%s:%d,%d,%d", worldName, x, y, z);
	}

	public static GenericLocation fromString(String serialized) {
		try {
			String[] parts = serialized.split(":");
			String world = parts[0];
			String[] coords = parts[1].split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			return new GenericLocation(x, y, z, world);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid GenericLocation string: " + serialized);
		}
	}
}