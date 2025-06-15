# BlockQuota

Un plugin Minecraft Spigot pour limiter le minage de blocs par joueur sur votre serveur.

## ✨ Fonctionnalités

- Limitez le nombre de blocs minés par joueur (par type de bloc)
- Stockage en base de données SQLite
- Messages configurables (`lang.yml`)
- Commande administrateur pour réinitialiser les quotas
- Compatible version 1.20 et 1.21
- Dépendance : Requiert LuckPerms pour gérer les permissions.

## 📦 Installation

1. Téléchargez le plugin `BlockQuota.jar`
2. Placez-le dans le dossier `plugins/` de votre serveur
3. Redémarrez le serveur
4. Configurez les limites dans `config.yml` et les messages dans `lang.yml`

---

## 

## ⚙️ Configurations

### `config.yml`

```yaml
block_limits:
  DIAMOND_ORE: 5
  ANCIENT_DEBRIS: 1
  EMERALD_ORE: 10
  # Ajoutez d'autres blocs ici
  
reset_time: "00:00" # heure:min
  
```

### `lang.yml`

```yaml

limit-reached: "&cTu as atteint la limite de minage pour &e%block%&c !"
block-break-added: "&a+1 &7%block% miné. &8(&7%current%/%limit%&8)"
limit-reset: "&bTes quotas de minage ont été réinitialisés."
limit-reset-another: "&bLes quotas de minage ont été réinitialisés pour &e%player%&b."
limit-reset-all: "&bLes quotas de minage ont été réinitialisés pour tous le monde."
not-connected: "&cLe joueur &e%player% &cn'est pas connecté."
quota-check: "&eTu as miné &a%current% &e%block% &7(sur &c%limit%&7 autorisés)."
not-whitelisted: "&cCe bloc n'est pas concerné par les quotas."
reload-success: "&aConfiguration rechargée !"
no-permission: "&cTu n'as pas la permission d'utiliser cette commande."
quota-set: "&7Le quota pour &e%player% &7sur le bloc &b%block% &7a été défini à &a%limit%&7."
quota-view-other: "&e%player% &7a miné &a%current% &e%block% &7(sur &c%limit%&7 autorisés)."

FACTION_HELP:
  1:
    - "&6&m-----------------------------------------------------"
    - "&9BlockQuota Help &e(Page 1/1)"
    - "&e/bq reload - &7Recharge les configurations du plugin."
    - "&e/bq reset - &7Réinitialise le quota d'un joueur."
    - "&eYou are currently on &fPage 1/1."
    - "&eTo view other pages, use &9/bq help <page>"
    - "&6&m-----------------------------------------------------"
```

## Permissions

- `blockquota.bypass` - Ne pas être affecté par les limites.
- `blockquota.reset`  - Réinitialiser les limites d'un joueur connecté.
- `blockquota.reload` - Recharger les configurations.