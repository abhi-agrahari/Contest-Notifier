import React from 'react';

const Logo = ({ size = 32, showText = true }) => {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
      <div 
        style={{ 
          width: size, 
          height: size, 
          background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #ec4899 100%)',
          borderRadius: '25%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          boxShadow: '0 8px 16px rgba(99, 102, 241, 0.3)',
          position: 'relative',
          overflow: 'hidden'
        }}
      >
        <svg 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="white" 
          strokeWidth="2.5" 
          strokeLinecap="round" 
          strokeLinejoin="round"
          style={{ width: '65%', height: '65%' }}
        >
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
          <circle cx="18" cy="6" r="3" fill="#ec4899" stroke="#ec4899" strokeWidth="1" />
        </svg>
      </div>
      {showText && (
        <span 
          style={{ 
            fontFamily: "'Outfit', sans-serif", 
            fontWeight: 800, 
            fontSize: size * 0.6,
            background: 'linear-gradient(to right, #fff, #94a3b8)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            letterSpacing: '-0.02em'
          }}
        >
          Contest Notifier
        </span>
      )}
    </div>
  );
};

export default Logo;
