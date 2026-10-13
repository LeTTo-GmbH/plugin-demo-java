# plugin-demo-java

Referenzimplementierung eines LeTTo-Plugins (Spring Boot, REST-Schnittstelle zum LeTTo-Server). Enthält das Beispiel-Plugin „Uhr".

## Dokumentation

Die öffentliche Doku dieses Plugins liegt **nicht** hier, sondern im LeTTo-Repo (GitHub Pages):
`../LeTTo/docs/wiki/Plugins/referenz-plugin-java/` (https://github.com/LeTTo-GmbH/LeTTo)

- Sie wird aus diesem Sourcecode mit dem Skill `/plugin-doku` erzeugt, der im LeTTo-Repo liegt
  (`../LeTTo/.claude/skills/plugin-doku/SKILL.md`). Dafür Claude Code im LeTTo-Repo starten –
  das LeTTo-Repo bindet dieses Repo über `additionalDirectories` ein.
- Die generierten Seiten tragen den Quell-Commit in einem HTML-Kommentar; der Skill aktualisiert damit inkrementell.
- Wenn du hier Endpunkte, DTOs, Enums, `BasePlugin`, den Build oder die Anleitung im `README.md` änderst:
  den Benutzer am Ende darauf hinweisen, dass die Doku mit `/plugin-doku` im LeTTo-Repo nachgezogen werden sollte.
- JavaDoc und Swagger-Annotationen (`@Operation`, `@Schema`) sind die Hauptquelle der generierten Doku –
  bei Änderungen an Controllern und DTOs pflegen.
