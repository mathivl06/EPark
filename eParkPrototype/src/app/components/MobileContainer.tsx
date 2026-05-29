import { ReactNode } from "react";

interface MobileContainerProps {
  children: ReactNode;
  withBottomNav?: boolean;
}

export function MobileContainer({ children, withBottomNav = false }: MobileContainerProps) {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div
        className="w-full max-w-md bg-white shadow-2xl relative overflow-hidden"
        style={{ height: "844px", maxHeight: "100vh" }}
      >
        <div className={`h-full overflow-y-auto ${withBottomNav ? "pb-14" : ""}`}>
          {children}
        </div>
      </div>
    </div>
  );
}
