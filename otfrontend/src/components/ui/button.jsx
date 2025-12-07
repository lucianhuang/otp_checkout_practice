export function Button({ children, variant = 'default', className = '', ...props }) {
  const base = 'rounded-lg px-4 py-2 font-medium transition';
  const variants = {
    default: 'bg-primary hover:bg-primary text-bg', // 找到橘色設定
    outline: 'border border-primary text-primary hover:bg-primary hover:text-bg',
    secondary: 'bg-bg hover:bg-accent text-text',
  };
  return (
    <button className={`${base} ${variants[variant]} ${className}`} {...props}>
      {children}
    </button>
  );
}