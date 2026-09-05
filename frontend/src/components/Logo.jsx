import React from 'react';

const Logo = ({ showText = true }) => {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 'bold', fontSize: '1.2rem' }}>
      <span role="img" aria-label="bell">🔔</span>
      {showText && <span>Contest Notifier</span>}
    </div>
  );
};

export default Logo;
