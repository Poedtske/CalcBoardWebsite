import React, { useEffect } from 'react';
import styles from '../dirigent/dirigent.module.css';

export default function Dirigent() {
  // Set the document title on component mount
  useEffect(() => {
    document.title = 'Dirigent';
  }, []);

  return (
    <main className={styles.main}>
        <section>
            <img src="/images/foto_andreas.jpg" alt="foto Andreas Carlier" />
        </section>
        <section>
            <h1 className={styles.h1}>Andreas Carlier</h1>
            <p>
            Andreas Carlier (°1998) begon op vijfjarige leeftijd met
    trompet. De eerste lessen kreeg hij van zijn vader en
    trombonist Gunter Carlier. Hij nam al snel de beslissing om
    aan de kunsthumaniora van het Lemmensinstituut te
    studeren.
            </p>
            <p>Na een overstap naar tuba, in de zomer van 2019,
            studeerde hij aan het Koninklijk Conservatorium van
            Antwerpen (B) en het Koninklijk Conservatorium van Brussel
            (B) bij Stephan Vanaenrode waar Andreas afstudeerde met
            Maxima Cum Laude voor tuba.</p>
            <p></p>
            <p>
                Op professioneel vlak is Andreas sinds 2021 verbonden aan
    de Koninklijke Muziekkapel van de Belgische Gidsen.
    Andreas is te horen op verscheidene cd-opnames en
    speelde reeds concerten in de meeste gerenommeerde
    concertzalen in binnen- en buitenland.
            </p>
            <p></p>
            <p>
                Als freelance-muzikant was Andreas reeds te horen met het orkest van de Koninklijke
    Muntschouwburg, Nationaal Orkest van België, Antwerp Symphony Orchestra, Belgian Brass, I
    Solisti Del Vento, Le Concert Olympique, National Youth Orchestra ... met topdirigenten als: Vasily
    Petrenko, Valeri Gergiev, Benjamin Zander, Jos Van Immerseel, Xian Zhang...
            </p>
            <p></p>
            <p>
                Na het winnen van een auditie was Andreas vast lid van het wereld bekende, gerenommeerde
    orkest: European Union Youth Orchestra (EUYO) waarmee hij doorheen Europa op tournee ging.
    Ook is hij sinds 2020, Solo-Tuba van het Youth Orchestra Flanders. Ook binnen de HaFaBracultuur is Andreas vast lid van Brassband Willebroek waarmee hij verscheidene (inter)nationale
    titels wist te bemachtigen.
            </p>
        </section>
    </main>
  );
}
