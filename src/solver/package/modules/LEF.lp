1{ aff(A, O) : object(O) }1 :- agent(A).

% Un objet O ne peut être affecté qu'une seule fois.
:- aff(A1, O), aff(A2, O), A1 != A2.

% Pas de jalousie entre voisins.
:- aff(A1, O1), aff(A2, O2), 
	position(A1, O1, P1), 
	position(A1, O2, P2), 
	P2 < P1, |A1-A2|==1.

#show aff/2.