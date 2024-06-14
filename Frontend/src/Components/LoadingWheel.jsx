import React from 'react';
import '../index.css';

const colors = [
  '#FFC107', '#FF5722', '#FF9800', '#FFEB3B', 
  '#CDDC39', '#8BC34A', '#4CAF50', '#00BCD4', 
  '#03A9F4', '#2196F3', '#673AB7', '#E91E63'
];

const LoadingWheel = () => {
  return (
    <div className="wheel ">
      {colors.map((color, index) => (
        <div 
          key={index} 
          className="segment" 
          style={{ 
            backgroundColor: color, 
            transform: `rotate(${index * 30}deg)` 
          }} 
        />
      ))}
    </div>
  );
}
export default LoadingWheel;