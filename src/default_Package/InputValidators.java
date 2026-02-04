package default_Package;

public final class InputValidators {
	private InputValidators() {}

	public static boolean isValidIPv4Format(String ip) {
		if (ip == null) return false;
		String[] parts = ip.trim().split("\\.");
		if (parts.length != 4) return false;

		for (String p : parts) {
			if (p.isEmpty()) return false;
			// refuse +, -, spaces, etc.
			for (int i = 0; i < p.length(); i++) {
				char c = p.charAt(i);
				if (c < '0' || c > '9') return false;
			}
			try {
				int v = Integer.parseInt(p);
				if (v < 0 || v > 255) return false;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	public static Integer parsePortInRange(String s, int minInclusive, int maxInclusive) {
		if (s == null) return null;
		try {
			int p = Integer.parseInt(s.trim());
			if (p < minInclusive || p > maxInclusive) return null;
			return p;
		} catch (NumberFormatException e) {
			return null;
		}
	}
}

