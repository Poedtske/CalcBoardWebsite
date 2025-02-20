import React from 'react';
import styles from './instrumenten.module.css';

const Instrumenten = () => {
  return (
    <main className={styles.main}>
      
      {/* Koperblazers Section */}
      <div className={styles.category}>
        <h1 className={styles.category_title}>Koperblazers</h1>
        <div className={styles.category_items}>
          <div className={styles.item}>
            <p>Mib Cornet</p>
            <img src="/images/instruments/cornet.png" alt="Cornet" />
          </div>
          <div className={styles.item}>
            <p>Trompet</p>
            <img src="/images/instruments/trompet.png" alt="Trompet" />
          </div>
          <div className={styles.item}>
            <p>Bugel</p>
            <img src="/images/instruments/bugel.png" alt="Bugel" />
          </div>
          <div className={styles.item}>
            <p>Altohoorn</p>
            <img src="/images/instruments/altohoorn.png" alt="Altohoorn" />
          </div>
          <div className={styles.item}>
            <p>Franse Hoorn</p>
            <img src="/images/instruments/franse hoorn.png" alt="Franse Hoorn" />
          </div>
          <div className={styles.item}>
            <p>Trombone</p>
            <img src="/images/instruments/trombone.png" alt="Trombone" />
          </div>
          <div className={styles.item}>
            <p>Bastrombone</p>
            <img src="/images/instruments/bastrombone.png" alt="Bastrombone" />
          </div>
          <div className={styles.item}>
            <p>Bariton</p>
            <img src="/images/instruments/bariton.png" alt="Bariton" />
          </div>
          <div className={styles.item}>
            <p>Euphonium</p>
            <img src="/images/instruments/euphonium.png" alt="Euphonium" />
          </div>
          <div className={styles.item}>
            <p>Mib Bas</p>
            <img src="/images/instruments/Mib bas.png" alt="Mib Bas" />
          </div>
          <div className={styles.item}>
            <p>Sib Bas</p>
            <img src="/images/instruments/Sib bas.png" alt="Sib Bas" />
          </div>
        </div>
      </div>

      {/* Houtblazers Section */}
      <div className={styles.category}>
        <h1 className={styles.category_title}>Houtblazers</h1>
        <div className={styles.category_items}>
          <div className={styles.item}>
            <p>Sax Sopraan</p>
            <img src="/images/instruments/sopraansax.png" alt="Sopraan Sax" />
          </div>
          <div className={styles.item}>
            <p>Sax Alto</p>
            <img src="/images/instruments/altsax.png" alt="Alt Sax" />
          </div>
          <div className={styles.item}>
            <p>Sax Tenor</p>
            <img src="/images/instruments/tenorsax.png" alt="Tenor Sax" />
          </div>
          <div className={styles.item}>
            <p>Sax Bariton</p>
            <img src="/images/instruments/saxbariton.png" alt="Sax Bariton" />
          </div>
          <div className={styles.item}>
            <p>Sax Bas</p>
            <img src="/images/instruments/SaxBas.png" alt="Sax Bas" />
          </div>
        </div>
      </div>

      {/* Slagwerk Section */}
      <div className={styles.category}>
        <h1 className={styles.category_title}>Slagwerk</h1>
        <div className={styles.category_items}>
          <div className={styles.item}>
            <p>Drumstel</p>
            <img src="/images/instruments/drumstel.png" alt="Drumstel" />
          </div>
          <div className={styles.item}>
            <p>Melodisch Slagwerk</p>
            <img src="/images/instruments/melodisch slagwerk.png" alt="Melodisch Slagwerk" />
          </div>
          <div className={styles.item}>
            <p>Pauken</p>
            <img src="/images/instruments/pauken.png" alt="Pauken" />
          </div>
          <div className={styles.item}>
            <p>Percussie</p>
            <img src="/images/instruments/percussie.png" alt="Percussie" />
          </div>
        </div>
      </div>

    </main>
  );
};

export default Instrumenten;
