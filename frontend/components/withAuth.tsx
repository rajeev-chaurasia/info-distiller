'use client';

import React, {useEffect, useState} from 'react';
import {useRouter} from 'next/navigation';

const withAuth = <P extends object>(WrappedComponent: React.ComponentType<P>) => {
    return (props: P) => {
      const router = useRouter();
      const [isLoading, setIsLoading] = useState(true);

      useEffect(() => {
          const token = localStorage.getItem('authToken');
          if (!token) {
              router.replace('/login');
          } else {
              setIsLoading(false);
          }
      }, [router]);

      if (isLoading) {
          return <p className="text-center mt-10">Loading...</p>;
      }

      return <WrappedComponent {...props} />;
  };
};

export default withAuth;