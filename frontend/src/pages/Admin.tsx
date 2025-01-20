import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {Loader, Loader2, PlusIcon} from 'lucide-react';
import {Button} from "../components/ui/button/Button";
import InputShowcase from "../components/ui/input/InputShowcase";
import ButtonShowcase from "../components/ui/button/ButtonShowcase";
import CardShowcase from "../components/ui/card/CardShowcase";

const AdminPage = () => {
    // const isAdmin = localStorage.getItem('role') === 'ADMIN';
    const navigate = useNavigate();



    return (
        <div className="space-y-4">
            <ButtonShowcase/>
            <InputShowcase/>
            <CardShowcase/>
        </div>
    );
};

export default AdminPage;