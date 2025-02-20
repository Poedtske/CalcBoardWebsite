import React from 'react';
import styles from '../bestuur/bestuur.module.css'; // Assuming you have a CSS file for styling

const bestuurMembers = [
  { name: 'Ruben Fransen', image: 'icoonManBestuur.png' },
  { name: 'Lance Wauters', image: 'icoonManBestuur.png' },
  { name: 'Seppe Mariën', image: 'icoonManBestuur.png' },
  { name: 'Ilse De Wachter', image: 'icoonVrouwBestuur.png' },
  { name: 'Jenny Freeman', image: 'icoonVrouwBestuur.png' },
  { name: 'Marina Rosic', image: 'icoonVrouwBestuur.png' },
  { name: 'Pascale Gysenbergs', image: 'icoonVrouwBestuur.png' },
  { name: 'Wim Mariën', image: 'icoonManBestuur.png' },
  { name: 'Chris Selleslagh', image: 'icoonManBestuur.png' },
  { name: 'Marc De Wael', image: 'icoonManBestuur.png' }
];

const Bestuur = () => {
  return (
    <main className={styles.main}>
        <section>
      <div className={styles.flex_container}>
        {bestuurMembers.map((member, index) => (
          <div  className={styles.flex_item} key={index}>
            <img src={`/images/${member.image}`} alt={member.name} />
            <p>{member.name}</p>
          </div>
        ))}
      </div>

      {/* You can add these buttons if needed */}
      {/* <div className=".container">
        <a href="/contact">
          <button style={{ backgroundColor: 'black' }} className="create flex-item">Contact us</button>
        </a>
      </div>
      <div className=".container">
        <a href="/members">
          <button style={{ backgroundColor: 'black' }} className="create flex-item">Member List</button>
        </a>
      </div> */}
    </section>
    </main>
  );
};

export default Bestuur;
