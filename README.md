# BlockQuota

Un plugin Minecraft Spigot pour limiter le minage de blocs par joueur sur votre serveur.

## ✨ Fonctionnalités

- Limitez le nombre de blocs minés par joueur (par type de bloc)
- Stockage en base de données SQLite
- Messages configurables (`lang.yml`)
- Commandes administrateur pour voir, réinitialiser ou modifier les quotas
- Compatible version 1.20 et 1.21

## 📦 Installation

1. Téléchargez le plugin `BlockQuota.jar`
2. Placez-le dans le dossier `plugins/` de votre serveur
3. Redémarrez le serveur
4. Configurez les limites dans `config.yml` et les messages dans `lang.yml`

---

## ⚙️ Configuration

### `config.yml`

```yaml
block_limits:
  DIAMOND_ORE: 5
  ANCIENT_DEBRIS: 1
  EMERALD_ORE: 10
  # Ajoutez d'autres blocs ici
  
reset_time: "00:00" # heure:min
  
```

## Permission

- `blockquota.bypass` - Ne pas être affecté par les limites.
- `blockquota.reset`  - Réinitialiser les limites d'un joueur connecté.
- `blockquota.reload` - Recharger les configurations.