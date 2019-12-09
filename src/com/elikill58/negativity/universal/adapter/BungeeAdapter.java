package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elikill58.negativity.bungee.BungeeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeAdapter extends Adapter {

	private Configuration config;
	private Plugin pl;
	private final HashMap<String, Configuration> LANGS = new HashMap<>();

	public BungeeAdapter(Plugin pl, Configuration config) {
		this.pl = pl;
		this.config = config;
	}

	@Override
	public String getName() {
		return "bungee";
	}

	@Override
	public Object getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public String getStringInConfig(String dir) {
		return config.getString(dir);
	}

	@Override
	public boolean getBooleanInConfig(String dir) {
		return config.getBoolean(dir);
	}

	@Override
	public void log(String msg) {
		pl.getLogger().info(msg);
	}

	@Override
	public void warn(String msg) {
		pl.getLogger().warning(msg);
	}

	@Override
	public void error(String msg) {
		pl.getLogger().severe(msg);
	}

	@Override
	public HashMap<String, String> getKeysListInConfig(String dir) {
		HashMap<String, String> list = new HashMap<>();
		for (String s : config.getSection(dir).getKeys())
			list.put(s, config.getString(dir + "." + s));
		return list;
	}

	@Override
	public int getIntegerInConfig(String dir) {
		return config.getInt(dir);
	}

	@Override
	public void set(String dir, Object value) {
		config.set(dir, value);
	}

	@Override
	public double getDoubleInConfig(String dir) {
		return config.getDouble(dir);
	}

	@Override
	public List<String> getStringListInConfig(String dir) {
		return config.getStringList(dir);
	}

	@Override
	public String getStringInOtherConfig(Path relativeFile, String key, String defaultValue) {
		Path configFile = pl.getDataFolder().toPath().resolve(relativeFile);
		if (Files.notExists(configFile))
			return defaultValue;
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile.toFile()).getString(key, defaultValue);
		} catch (IOException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	@Override
	public File copy(String lang, File f) {
		if (f.exists())
			return f;
		String fileName = "bungee_en_US.yml";
		if (lang.toLowerCase().contains("fr") || lang.toLowerCase().contains("be"))
			fileName = "bungee_fr_FR.yml";
		if (lang.toLowerCase().contains("pt") || lang.toLowerCase().contains("br"))
			fileName = "bungee_pt_BR.yml";
		if (lang.toLowerCase().contains("no"))
			fileName = "bungee_no_NO.yml";
		else if (lang.toLowerCase().contains("ru"))
			fileName = "bungee_ru_RU.yml";
		else if (lang.toLowerCase().contains("zh") || lang.toLowerCase().contains("cn"))
			fileName = "bungee_zh_CN.yml";
		else if (lang.toLowerCase().contains("de"))
			fileName = "bungee_de_DE.yml";
		else if (lang.toLowerCase().contains("nl"))
			fileName = "bungee_nl_NL.yml";
		else if (lang.toLowerCase().contains("sv"))
			fileName = "bungee_sv_SV.yml";
		try (InputStream in = pl.getResourceAsStream(fileName); OutputStream out = new FileOutputStream(f)) {
			ByteStreams.copy(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

	@Override
	public void loadLang() {
		File langDir = new File(pl.getDataFolder().getAbsolutePath() + File.separator + "lang" + File.separator);
		if (!langDir.exists())
			langDir.mkdirs();

		try {
			if (!TranslatedMessages.activeTranslation) {
				String defaultLang = TranslatedMessages.DEFAULT_LANG;
				LANGS.put(defaultLang, ConfigurationProvider.getProvider(YamlConfiguration.class)
						.load(copy(defaultLang, new File(langDir.getAbsolutePath() + "/" + defaultLang + ".yml"))));
				return;
			}

			for (String l : TranslatedMessages.LANGS)
				LANGS.put(l, ConfigurationProvider.getProvider(YamlConfiguration.class)
						.load(copy(l, new File(langDir.getAbsolutePath() + "/" + l + ".yml"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getStringFromLang(String lang, String key) {
		return LANGS.get(lang).getString(key);
	}

	@Override
	public List<String> getStringListFromLang(String lang, String key) {
		return LANGS.get(lang).getStringList(key);
	}

	@Override
	public List<Cheat> getAbstractCheats() {
		return new ArrayList<>();
	}

	@Override
	public void reload() {

	}

	@Override
	public Object getItem(String itemName) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getVersion() {
		return ProxyServer.getInstance().getGameVersion();
	}

	@Override
	public void reloadConfig() {

	}

	@Nonnull
	@Override
	public NegativityAccount getNegativityAccount(UUID playerId) {
		return new NegativityAccount(playerId, TranslatedMessages.getLang(playerId), false, new ArrayList<>());
	}

	@Nullable
	@Override
	public NegativityPlayer getNegativityPlayer(UUID playerId) {
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerId);
		return player != null ? BungeeNegativityPlayer.getNegativityPlayer(player) : null;
	}

	@Override
	public void invalidateAccount(UUID playerId) {}

	@Override
	public void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, String hover_proof) {}

	@Override
	public void runConsoleCommand(String cmd) {
		pl.getProxy().getPluginManager().dispatchCommand(pl.getProxy().getConsole(), cmd);
	}
}
